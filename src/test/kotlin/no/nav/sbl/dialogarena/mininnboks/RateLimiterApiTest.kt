package no.nav.sbl.dialogarena.mininnboks

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.AnythingPattern
import kotlinx.coroutines.runBlocking
import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.consumer.RateLimiterApi
import no.nav.sbl.dialogarena.mininnboks.consumer.RateLimiterApiImpl
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.slf4j.MDC
import org.spekframework.spek2.Spek

private val token = "TOKEN"

object RateLimiterApiTest : Spek({
    MDC.put(MDCConstants.MDC_CALL_ID, "MDC_CALL_ID")

    test("henter ut ratelimiter status") {
        withMockGateway(
            stub = postWithBody(statusCode = 200, body = "false")
        ) { rateLimiterApi ->
            val response = rateLimiterApi.oppdatereRateLimiter(token)
            assertThat(response, `is`(false))
        }
    }

    test("handterer status coder utenfor 200-299 rangen") {
        withMockGateway(
            stub = postWithBody(statusCode = 404)
        ) { rateLimiterApi ->
            val response = rateLimiterApi.oppdatereRateLimiter(token)
            assertThat(response, `is`(true))
        }

        withMockGateway(
            stub = postWithBody(statusCode = 500, body = "")
        ) { rateLimiterApi ->
            val response = rateLimiterApi.oppdatereRateLimiter(token)
            assertThat(response, `is`(true))
        }
    }
})

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

private fun withMockGateway(
    stub: WireMockServer.() -> Unit = { },
    test: suspend (RateLimiterApi) -> Unit
) {
    val wireMockServer = WireMockServer()
    try {
        stub(wireMockServer)
        wireMockServer.start()

        val client = RateLimiterApiImpl("http://localhost:${wireMockServer.port()}")
        runBlocking {
            test(client)
        }

        wireMockServer.verify(
            postRequestedFor(urlEqualTo("/rate-limiter/api/limit"))
                .withHeader("X-Correlation-ID", AnythingPattern())
                .withHeader("Authorization", AnythingPattern())
                .withHeader("Accept", matching("application/json"))
                .withHeader("Content-Type", matching("application/json"))
        )
    } finally {
        wireMockServer.stop()
    }
}
