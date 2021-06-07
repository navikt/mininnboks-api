package no.nav.sbl.dialogarena.mininnboks

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.consumer.RateLimiterApi
import no.nav.sbl.dialogarena.mininnboks.consumer.RateLimiterApiImpl
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.slf4j.MDC
import org.spekframework.spek2.Spek

internal class RateLimiterApiTest : Spek({
    val token = "TOKEN"
    MDC.put(MDCConstants.MDC_CALL_ID, "MDC_CALL_ID")

    test("er det ok å sende spørsmålet") {
        withMockGateway(stub = getWithBody(statusCode = 200, body = "true")) { rateLimiterApi ->
            val response = rateLimiterApi.oppdatereRateLimiter(token)
            assertThat(response, `is`(true))
        }
    }

    test("handterer status coder utenfor 200-299 rangen") {
        withMockGateway(stub = getWithBody(statusCode = 404)) { rateLimiterApi ->
            val response = rateLimiterApi.oppdatereRateLimiter(token)
            assertThat(response, `is`(true))
        }

        withMockGateway(
            verify = { server -> verifyHeaders(server, postRequestedFor(urlEqualTo("/rate-limiter/api/limit"))) },
            stub = postWithBody(statusCode = 500, body = "")
        ) { rateLimiterApi ->
            val response = rateLimiterApi.oppdatereRateLimiter(token)
            assertThat(response, `is`(true))
        }
    }
})

private fun getWithBody(statusCode: Int = 200, body: String? = null): WireMockServer.() -> Unit = {
    this.stubFor(get(anyUrl()).withBody(statusCode, body))
}

private fun postWithBody(statusCode: Int = 200, body: String? = null): WireMockServer.() -> Unit = {
    this.stubFor(post(anyUrl()).withBody(statusCode, body))
}

private fun MappingBuilder.withBody(statusCode: Int = 200, body: String? = null): MappingBuilder {
    this.willReturn(
        aResponse()
            .withStatus(statusCode)
            .withHeader("Content-Type", "application/json")
            .withBody(body)
    )
    return this
}

private fun verifyHeaders(server: WireMockServer, call: RequestPatternBuilder): () -> Unit = {
    server.verify(
        call
            .withHeader("X-Correlation-ID", AnythingPattern())
            .withHeader("Authorization", AnythingPattern())
            .withHeader("accept", matching("application/json"))
    )
}

private fun withMockGateway(
    stub: WireMockServer.() -> Unit = { },
    verify: ((WireMockServer) -> Unit)? = null,
    test: (RateLimiterApi) -> Unit
) {
    val wireMockServer = WireMockServer()
    try {
        stub(wireMockServer)
        wireMockServer.start()

        val client = RateLimiterApiImpl("http://localhost:${wireMockServer.port()}")
        test(client)

        if (verify == null) {
            verifyHeaders(wireMockServer, getRequestedFor(urlEqualTo("/rate-limiter/api/limit")))
        } else {
            verify(wireMockServer)
        }
    } finally {
        wireMockServer.stop()
    }
}
