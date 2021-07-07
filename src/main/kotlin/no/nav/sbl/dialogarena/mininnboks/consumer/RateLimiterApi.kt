package no.nav.sbl.dialogarena.mininnboks.consumer

import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.common.log.MDCConstants
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.mininnboks.JacksonUtils
import org.slf4j.MDC
import java.util.*

interface RateLimiterApi {
    suspend fun oppdatereRateLimiter(idToken: String): Boolean
}

class RateLimiterApiImpl(
    private val baseUrl: String = EnvironmentUtils.getRequiredProperty("RATE_LIMITER_URL"),
    private val client: HttpClient
) : RateLimiterApi {
    private val objectMapper = JacksonUtils.objectMapper

    override suspend fun oppdatereRateLimiter(idToken: String): Boolean {
        val request: HttpRequestBuilder.() -> Unit = {
            url("$baseUrl/rate-limiter/api/limit")
            method = HttpMethod.Post
            contentType(ContentType.Application.Json)
            header("X-Correlation-ID", MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString())
            header("Authorization", "Bearer $idToken")
            body = ""
        }

        return try {
            fetch(request)
        } catch (e: IllegalStateException) {
            return true
        }
    }

    private suspend inline fun <reified RESPONSE> fetch(block: HttpRequestBuilder.() -> Unit): RESPONSE {
        val response: HttpResponse = client.request(block)
        if (response.status.isSuccess()) {
            val body : String = response.receive() ?: throw IllegalStateException("Forventet body fra rate-limiter, men fikk: null")
            return objectMapper.readValue(body)
        }else {
            throw IllegalStateException("Forventet 200-range svar, men fikk: ${response.status.value}")
        }
    }
}
