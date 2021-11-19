package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.http.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.JacksonUtils
import no.nav.sbl.dialogarena.mininnboks.TestUtils.MOCK_SUBJECT
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.queries.HentAdressebeskyttelse
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import no.nav.sbl.dialogarena.mininnboks.dummySubject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.core.Is
import org.hamcrest.core.IsNull.nullValue
import org.slf4j.MDC
import org.spekframework.spek2.Spek
import java.util.*

private val configuration: Configuration = mockk()

object PdlServicetest : Spek({

    beforeEachTest {
        coEvery { configuration.PDL_API_URL } returns "https://test.pdl.nav.no"
        coEvery { configuration.PDL_API_APIKEY } returns "PDL_API_API_VALUE"
    }

    test("henter adressebeskyttelsegradering om det finnes") {
        runBlocking {
            val harAdressebeskyttelse = gittGradering(HentAdressebeskyttelse.AdressebeskyttelseGradering.UGRADERT)
                .hentAdresseBeskyttelse(dummySubject)

            assertThat(
                harAdressebeskyttelse,
                Matchers.`is`(HentAdressebeskyttelse.AdressebeskyttelseGradering.UGRADERT)
            )
        }
    }

    test("henter null om adressebeskyttelsegradering ikke finnes") {
        runBlocking {
            val harAdressebeskyttelse = gittGradering(null).hentAdresseBeskyttelse(dummySubject)
            assertThat(harAdressebeskyttelse, Matchers.`is`(nullValue()))
        }
    }

    test("sjekk for kode6") {
        runBlocking {
            val pdlService = gittGradering(HentAdressebeskyttelse.AdressebeskyttelseGradering.STRENGT_FORTROLIG)
            assertThat(pdlService.harKode6(MOCK_SUBJECT), Matchers.`is`(true))
        }

        runBlocking {
            val pdlService = gittGradering(HentAdressebeskyttelse.AdressebeskyttelseGradering.STRENGT_FORTROLIG)
            assertThat(pdlService.harStrengtFortroligAdresse(MOCK_SUBJECT), Matchers.`is`(true))
        }

        runBlocking {
            val pdlService = gittGradering(null)
            assertThat(pdlService.harKode6(MOCK_SUBJECT), Matchers.`is`(false))
        }

        runBlocking {
            val pdlService = gittGradering(null)
            assertThat(pdlService.harStrengtFortroligAdresse(MOCK_SUBJECT), Matchers.`is`(false))
        }
    }

    test("sjekk for kode7") {
        runBlocking {
            val pdlService = gittGradering(HentAdressebeskyttelse.AdressebeskyttelseGradering.FORTROLIG)
            assertThat(pdlService.harKode7(MOCK_SUBJECT), Matchers.`is`(true))
        }

        runBlocking {
            val pdlService = gittGradering(HentAdressebeskyttelse.AdressebeskyttelseGradering.FORTROLIG)
            assertThat(pdlService.harFortroligAdresse(MOCK_SUBJECT), Matchers.`is`(true))
        }

        runBlocking {
            val pdlService = gittGradering(null)
            assertThat(pdlService.harKode7(MOCK_SUBJECT), Matchers.`is`(false))
        }

        runBlocking {
            val pdlService = gittGradering(null)
            assertThat(pdlService.harFortroligAdresse(MOCK_SUBJECT), Matchers.`is`(false))
        }
    }

    test("feil blir pakket inn i egen exceptiontype") {
        gittUrlTilPdl()
        val client = gittClientSomSvarer(body = gittErrorPdlResponse("Det skjedde en feil"))
        val stsService = gittStsService()
        val pdlService = PdlService(stsService, configuration, client = client, graphQLHttpClient = client)
        try {
            runBlocking {
                pdlService.hentAdresseBeskyttelse(dummySubject)
            }
        } catch (t: Throwable) {
            assertThat(t.message, Is.`is`("Kunne ikke utlede adressebeskyttelse"))
        }
    }

    test("skal hente ut alle adresser for bruker") {
        runBlocking {
            val pdlService = gittAdresserData()
            val adresse = pdlService.hentFolkeregistrertAdresse(MOCK_SUBJECT)

            assertThat(
                adresse,
                Matchers.`is`(
                    Adresse(
                        adresse = "Kirkegata",
                        tilleggsnavn = "H0101 Storgården",
                        husnummer = "12",
                        husbokstav = "B",
                        kommunenummer = "4321",
                        postnummer = "1234",
                        type = Adresse.Type.GATEADRESSE
                    )
                )
            )
        }
    }
})

private fun gittClientSomSvarer(body: String = ""): HttpClient {
    return HttpClient(MockEngine) {
        install(JsonFeature) {
            serializer = JacksonSerializer(JacksonUtils.objectMapper)
        }
        engine {
            addHandler { request ->
                when (request.url.toString()) {
                    "https://test.pdl.nav.no/graphql" -> {
                        val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Application.Json.toString()))
                        respond(body, headers = responseHeaders)
                    }
                    else -> error("Unhandled ${request.url}")
                }
            }
        }
    }
}

private fun gittStsService(token: String = UUID.randomUUID().toString()): SystemuserTokenProvider {
    val stsService = mockk<SystemuserTokenProvider>()

    coEvery { stsService.getSystemUserAccessToken() } returns (token)

    return stsService
}

private fun gittUrlTilPdl() {
    MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
}

private fun gittOkAdressebeskyttelseResponse(gradering: HentAdressebeskyttelse.AdressebeskyttelseGradering? = null): String {
    return """
            { 
                "data": {
                    "hentPerson": {
                        "adressebeskyttelse": [
                            ${if (gradering != null) "{ \"gradering\": \"$gradering\" }" else ""}
                        ]
                    }
                }
            }
    """.trimIndent()
}

private fun gittOkAdresserResponse(): String {
    return """
        {
            "data": {
                "hentPerson": {
                    "bostedsadresse": [
                        {
                            "vegadresse": {
                                "matrikkelId": "123456789",
                                "adressenavn": "Kirkegata",
                                "husnummer": "12",
                                "husbokstav": "B",
                                "tilleggsnavn": "Storgården",
                                "postnummer": "1234",
                                "kommunenummer": "4321",
                                "bruksenhetsnummer": "H0101"
                            }
                        },
                        {
                            "matrikkeladresse": {
                                "matrikkelId": "123456789",
                                "postnummer": "1234",
                                "tilleggsnavn": "Storgården",
                                "kommunenummer": "4321",
                                "bruksenhetsnummer": "H0101"
                            }
                        }
                    ]
                }
            }
        }
    """.trimIndent()
}

private fun gittErrorPdlResponse(message: String): String {
    return """
        {
            "errors": [
                { "message": "$message"}
            ]
        }
    """.trimIndent()
}

private fun gittGradering(gradering: HentAdressebeskyttelse.AdressebeskyttelseGradering?): PdlService {
    gittUrlTilPdl()
    val client = gittClientSomSvarer(body = gittOkAdressebeskyttelseResponse(gradering))
    val stsService = gittStsService()
    return PdlService(stsService, configuration, client = client, graphQLHttpClient = client)
}

private fun gittAdresserData(): PdlService {
    gittUrlTilPdl()
    val client = gittClientSomSvarer(body = gittOkAdresserResponse())
    val stsService = gittStsService()
    return PdlService(stsService, configuration, client = client, graphQLHttpClient = client)
}
