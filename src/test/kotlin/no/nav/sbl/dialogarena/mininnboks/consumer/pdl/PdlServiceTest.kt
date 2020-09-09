package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import io.mockk.every
import io.mockk.mockk
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.TestUtils.MOCK_SUBJECT
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import okhttp3.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.MDC
import java.util.*


internal data class MockContext(
        val client: OkHttpClient,
        val response: Response
)

internal class PdlServiceTest {

    val configuration: Configuration = mockk()

    @BeforeEach
    fun setUp() {
        every { configuration.PDL_API_URL } returns "https://test.pdl.nav.no"
    }

    @Test
    fun `henter adressebeskyttelsegradering om det finnes`() {
        gittGradering(PdlAdressebeskyttelseGradering.UGRADERT) { (_, pdlService) ->
            val harAdressebeskyttelse = pdlService.hentAdresseBeskyttelse("anyfnr")
            assertThat(harAdressebeskyttelse).isEqualTo(PdlAdressebeskyttelseGradering.UGRADERT)
        }
    }

    @Test
    fun `henter null om adressebeskyttelsegradering ikke finnes`() {
        gittGradering(null) { (_, pdlService) ->
            val harAdressebeskyttelse = pdlService.hentAdresseBeskyttelse("anyfnr")
            assertThat(harAdressebeskyttelse).isNull()
        }
    }

    @Test
    fun `sjekk for kode6`() {
        gittGradering(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG) { (_, pdlService) ->
            assertThat(pdlService.harKode6("anyfnr")).isTrue()
        }

        gittGradering(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG) { (_, pdlService) ->
            assertThat(pdlService.harStrengtFortroligAdresse("anyfnr")).isTrue()
        }

        gittGradering(null) { (_, pdlService) ->
            assertThat(pdlService.harKode6("anyfnr")).isFalse()
        }

        gittGradering(null) { (_, pdlService) ->
            assertThat(pdlService.harStrengtFortroligAdresse("anyfnr")).isFalse()
        }
    }

    @Test
    fun `sjekk for kode7`() {
        gittGradering(PdlAdressebeskyttelseGradering.FORTROLIG) { (_, pdlService) ->
            assertThat(pdlService.harKode7("anyfnr")).isTrue()
        }

        gittGradering(PdlAdressebeskyttelseGradering.FORTROLIG) { (_, pdlService) ->
            assertThat(pdlService.harFortroligAdresse("anyfnr")).isTrue()
        }

        gittGradering(null) { (_, pdlService) ->
            assertThat(pdlService.harKode7("anyfnr")).isFalse()
        }

        gittGradering(null) { (_, pdlService) ->
            assertThat(pdlService.harFortroligAdresse("anyfnr")).isFalse()
        }

    }

    @Test
    fun `feil blir pakket inn i egen exceptiontype`() {
        gittUrlTilPdl()
        val mockContext = gittClientSomSvarer(body = gittErrorPdlResponse("Det skjedde en feil"))
        val stsService = gittStsService()
        val pdlService = PdlService(mockContext.client, stsService, configuration)

        // SubjectHandler.withSubject(MOCK_SUBJECT, UnsafeSupplier {
        assertThrows<PdlException> {
            pdlService.hentAdresseBeskyttelse("anyfnr")
        }
    }

    fun gittClientSomSvarer(status: Int = 200, body: String = "", throwException: Boolean = false): MockContext {
        val client = mockk<OkHttpClient>()

        val remoteCall = mockk<Call>()

        val request = mockk<Request>()

        val response: Response = Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(status)
                .message("test")
                .body(
                        ResponseBody.create(
                                MediaType.parse("application/json"),
                                body
                        ))
                .build()

        every { remoteCall.execute() } returns response
        every { client.newCall(any()) } returns (remoteCall)

        return MockContext(client, response)
    }

    fun gittStsService(token: String = UUID.randomUUID().toString()): SystemuserTokenProvider {
        val stsService = mockk<SystemuserTokenProvider>()

        every { stsService.getSystemUserAccessToken() } returns (token)

        return stsService
    }

    fun gittUrlTilPdl() {
        MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
        //EnvironmentUtils.setProperty(configuration.PDL_API_URL, url, EnvironmentUtils.Type.PUBLIC)
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

    fun gittGradering(gradering: PdlAdressebeskyttelseGradering?, fn: (Pair<MockContext, PdlService>) -> Unit) {
        gittUrlTilPdl()
        val mockContext = gittClientSomSvarer(body = gittOkPdlResponse(gradering))
        val stsService = gittStsService()
        val pdlService = PdlService(mockContext.client, stsService, configuration)

        SubjectHandler.withSubject(MOCK_SUBJECT) {
            fn(Pair(mockContext, pdlService))
        }
    }
}
