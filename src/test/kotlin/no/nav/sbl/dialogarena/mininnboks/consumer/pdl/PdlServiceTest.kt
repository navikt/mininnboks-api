package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.TestUtils.MOCK_SUBJECT
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
            val harAdressebeskyttelse = gittGradering(PdlAdressebeskyttelseGradering.UGRADERT).hentAdresseBeskyttelse(dummySubject)
            assertThat(harAdressebeskyttelse, Matchers.`is`(PdlAdressebeskyttelseGradering.UGRADERT))
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
            val pdlService = gittGradering(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG)
            assertThat(pdlService.harKode6(MOCK_SUBJECT), Matchers.`is`(true))
        }

        runBlocking {
            val pdlService = gittGradering(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG)
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
            val pdlService = gittGradering(PdlAdressebeskyttelseGradering.FORTROLIG)
            assertThat(pdlService.harKode7(MOCK_SUBJECT), Matchers.`is`(true))
        }

        runBlocking {
            val pdlService = gittGradering(PdlAdressebeskyttelseGradering.FORTROLIG)
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

    fun gittOkPdlResponse(gradering: PdlAdressebeskyttelseGradering? = null): String {
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

    fun gittErrorPdlResponse(message: String): String {
        return """
            {
                "errors": [
                    { "message": "$message"}
                ]
            }
        """.trimIndent()
    }

    fun gittGradering(gradering: PdlAdressebeskyttelseGradering?): PdlService {
        gittUrlTilPdl()
        val client = gittClientSomSvarer(body = gittOkPdlResponse(gradering))
        val stsService = gittStsService()
        return PdlService(client, stsService, configuration)
    }
}
