package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import com.nhaarman.mockitokotlin2.*
import no.nav.brukerdialog.security.domain.IdentType
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider
import no.nav.common.auth.SsoToken
import no.nav.common.auth.Subject
import no.nav.common.auth.SubjectHandler
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.config.ServiceConfig
import no.nav.sbl.util.EnvironmentUtils
import no.nav.sbl.util.fn.UnsafeSupplier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import java.util.*
import javax.ws.rs.client.Client
import javax.ws.rs.client.Invocation
import javax.ws.rs.client.WebTarget
import javax.ws.rs.core.Response

internal data class MockContext(
        val client: Client,
        val webTarget: WebTarget,
        val invocationBuilder: Invocation.Builder,
        val response: Response
)

internal class PdlServiceTest {
    val mockSubject: Subject = Subject("uid", IdentType.EksternBruker, SsoToken.oidcToken("token", mutableMapOf<String, Any>()))

    @Test
    fun `henter adressebeskyttelsegradering om det finnes`() {
        gittGradering(PdlAdressebeskyttelseGradering.UGRADERT) {(_, pdlService) ->
            val harAdressebeskyttelse = pdlService.hentAdresseBeskyttelse("anyfnr")
            assertThat(harAdressebeskyttelse).isEqualTo(PdlAdressebeskyttelseGradering.UGRADERT)
        }
    }

    @Test
    fun `henter null om adressebeskyttelsegradering ikke finnes`() {
        gittGradering(null) {(_, pdlService) ->
            val harAdressebeskyttelse = pdlService.hentAdresseBeskyttelse("anyfnr")
            assertThat(harAdressebeskyttelse).isNull()
        }
    }

    @Test
    fun `sjekk for kode6`() {
        gittGradering(PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG) {(_, pdlService) ->
            assertThat(pdlService.harKode6("anyfnr")).isTrue()
            assertThat(pdlService.harStrengtFortroligAdresse("anyfnr")).isTrue()
        }
    }

    @Test
    fun `sjekk for kode7`() {
        gittGradering(PdlAdressebeskyttelseGradering.FORTROLIG) {(_, pdlService) ->
            assertThat(pdlService.harKode7("anyfnr")).isTrue()
            assertThat(pdlService.harFortroligAdresse("anyfnr")).isTrue()
        }
    }

    @Test
    fun `håndterer feilmelding fra pdl`() {
        gittUrlTilPdl()
        val mockContext = gittClientSomSvarer(body = gittErrorPdlResponse("Det skjedde en feil"))
        val stsService = gittStsService()
        val pdlService = PdlService(mockContext.client, stsService)

        val adressebeskyttelseGradering : PdlAdressebeskyttelseGradering? = SubjectHandler.withSubject(mockSubject, UnsafeSupplier {
            pdlService.hentAdresseBeskyttelse("anyfnr")
        })

        assertThat(adressebeskyttelseGradering).isNull()
    }

    @Test
    fun `bruker http-options for ping`() {
        gittUrlTilPdl()
        val mockContext = gittClientSomSvarer()
        val stsService = gittStsService()
        val pdlService = PdlService(mockContext.client, stsService)

        val ping = pdlService.getHelsesjekk().ping()

        verify(mockContext.invocationBuilder, times(1)).options()
        assertThat(ping.erVellykket()).isTrue()
    }

    @Test
    fun `ping rapporterer feil ved annen statuskode`() {
        gittUrlTilPdl()
        val mockContext = gittClientSomSvarer(status = 199)
        val stsService = gittStsService()
        val pdlService = PdlService(mockContext.client, stsService)

        val ping = pdlService.getHelsesjekk().ping()

        verify(mockContext.invocationBuilder, times(1)).options()
        assertThat(ping.erVellykket()).isFalse()
        assertThat(ping.feilmelding).contains("199")
    }

    @Test
    fun `ping rapporterer feil ved exception`() {
        gittUrlTilPdl()
        val mockContext = gittClientSomSvarer(throwException = true)
        val stsService = gittStsService()
        val pdlService = PdlService(mockContext.client, stsService)

        val ping = pdlService.getHelsesjekk().ping()

        verify(mockContext.invocationBuilder, times(1)).options()
        assertThat(ping.erVellykket()).isFalse()
        assertThat(ping.feil).isNotNull()
    }

    fun gittClientSomSvarer(status: Int = 200, body: String = "", throwException: Boolean = false): MockContext {
        val client = mock<Client>()
        val webTarget = mock<WebTarget>()
        val invocationBuiler = mock<Invocation.Builder>()
        val response = mock<Response>()

        whenever(client.target(any<String>())).thenReturn(webTarget)
        whenever(webTarget.path(any())).thenReturn(webTarget)
        whenever(webTarget.request()).thenReturn(invocationBuiler)
        whenever(invocationBuiler.header(any(), any())).thenReturn(invocationBuiler)

        if (throwException) {
            whenever(invocationBuiler.options()).thenThrow(IllegalStateException())
            whenever(invocationBuiler.post(any())).thenThrow(IllegalStateException())
        } else {
            whenever(invocationBuiler.options()).thenReturn(response)
            whenever(invocationBuiler.post(any())).thenReturn(response)
        }

        whenever(response.status).thenReturn(status)
        whenever(response.readEntity(eq(String::class.java))).thenReturn(body)

        return MockContext(client, webTarget, invocationBuiler, response)
    }

    fun gittStsService(token: String = UUID.randomUUID().toString()): SystemUserTokenProvider {
        val stsService = mock<SystemUserTokenProvider>()

        whenever(stsService.token).thenReturn(token)

        return stsService
    }

    fun gittUrlTilPdl(url: String = "http://mock.com") {
        MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
        EnvironmentUtils.setProperty(ServiceConfig.PDL_API_URL, url, EnvironmentUtils.Type.PUBLIC)
    }

    fun gittOkPdlResponse(gradering: PdlAdressebeskyttelseGradering? = null): String {
        return """
            { 
                "data": {
                    "hentPerson": {
                        "adressebeskyttelse": [
                            ${ if (gradering != null) "{ \"gradering\": \"$gradering\" }" else "" }
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
        val pdlService = PdlService(mockContext.client, stsService)

        SubjectHandler.withSubject(mockSubject) {
            fn(Pair(mockContext, pdlService))
        }
    }
}
