package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.TestUtils.MOCK_SUBJECT
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.queries.HentAdressebeskyttelse
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import no.nav.sbl.dialogarena.mininnboks.dummySubject
import okhttp3.OkHttpClient
import okhttp3.mock.MediaTypes.MEDIATYPE_JSON
import okhttp3.mock.MockInterceptor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.core.IsNull.nullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.MDC
import java.util.*

class PdlServiceTest {

    val configuration: Configuration = mockk()

    @BeforeEach
    fun setUp() {
        coEvery { configuration.PDL_API_URL } returns "https://test.pdl.nav.no"
        coEvery { configuration.PDL_API_APIKEY } returns "PDL_API_API_VALUE"
    }

    @Test
    fun `henter adressebeskyttelsegradering om det finnes`() {
        runBlocking {
            val harAdressebeskyttelse = gittGradering(HentAdressebeskyttelse.AdressebeskyttelseGradering.UGRADERT)
                .hentAdresseBeskyttelse(dummySubject)

            assertThat(
                harAdressebeskyttelse,
                Matchers.`is`(HentAdressebeskyttelse.AdressebeskyttelseGradering.UGRADERT)
            )
        }
    }

    @Test
    fun `henter null om adressebeskyttelsegradering ikke finnes`() {
        runBlocking {
            val harAdressebeskyttelse = gittGradering(null).hentAdresseBeskyttelse(dummySubject)
            assertThat(harAdressebeskyttelse, Matchers.`is`(nullValue()))
        }
    }

    @Test
    fun `sjekk for kode6`() {
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

    @Test
    fun `sjekk for kode7`() {
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

    @Test
    fun `feil blir pakket inn i egen exceptiontype`() {
        gittUrlTilPdl()
        val client = gittClientSomSvarer(body = gittErrorPdlResponse("Det skjedde en feil"))
        val stsService = gittStsService()
        val pdlService = PdlService(client, stsService, configuration)

        assertThrows<PdlException> {
            runBlocking {
                pdlService.hentAdresseBeskyttelse(dummySubject)
            }
        }
    }

    @Test
    fun `skal hente ut alle adresser for bruker`() {
        runBlocking {
            val pdlService = gittAdresserData()
            val adresse = pdlService.hentFolkeregistrertAdresse(MOCK_SUBJECT)

            assertThat(
                adresse, Matchers.`is`(
                    Adresse(
                        adresse = "Kirkegata",
                        tilleggsnavn = "H0101 Storgården",
                        husnummer = "12",
                        husbokstav = "B",
                        kommunenummer = "4321",
                        postnummer = "1234",
                        type = "VEGADRESSE"
                    )
                )
            )
        }
    }


    private fun gittClientSomSvarer(body: String = ""): OkHttpClient {

        val interceptor = MockInterceptor()

        interceptor.addRule()
            .post()
            .url("https://test.pdl.nav.no/graphql")
            .respond(body, MEDIATYPE_JSON)

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    fun gittStsService(token: String = UUID.randomUUID().toString()): SystemuserTokenProvider {
        val stsService = mockk<SystemuserTokenProvider>()

        every { stsService.getSystemUserAccessToken() } returns (token)

        return stsService
    }

    fun gittUrlTilPdl() {
        MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
    }

    fun gittOkAdressebeskyttelseResponse(gradering: HentAdressebeskyttelse.AdressebeskyttelseGradering? = null): String {
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

    fun gittOkAdresserResponse(): String {
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

    fun gittErrorPdlResponse(message: String): String {
        return """
            {
                "errors": [
                    { "message": "$message"}
                ]
            }
        """.trimIndent()
    }

    fun gittGradering(gradering: HentAdressebeskyttelse.AdressebeskyttelseGradering?): PdlService {
        gittUrlTilPdl()
        val client = gittClientSomSvarer(body = gittOkAdressebeskyttelseResponse(gradering))
        val stsService = gittStsService()
        return PdlService(client, stsService, configuration)
    }

    fun gittAdresserData(): PdlService {
        gittUrlTilPdl()
        val client = gittClientSomSvarer(body = gittOkAdresserResponse())
        val stsService = gittStsService()
        return PdlService(client, stsService, configuration)
    }
}
