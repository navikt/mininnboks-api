package no.nav.sbl.dialogarena.mininnboks.consumer

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.mininnboks.JacksonUtils
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response

interface RateLimiterApi {
    fun oppdatereRateLimiter(idToken: String): Boolean
}

class RateLimiterApiImpl(
    private val baseUrl: String = EnvironmentUtils.getRequiredProperty("RATE_LIMITER_URL")

) : RateLimiterApi {
    private val objectMapper = JacksonUtils.objectMapper
    private val client = RestClient.baseClient().newBuilder().build()
    private val JSON: MediaType = requireNotNull(MediaType.parse("application/json; charset=utf-8"))

    override fun oppdatereRateLimiter(idToken: String): Boolean {
        val requestBody = RequestBody.create(
            JSON,
            objectMapper.writeValueAsString("")
        )
        val request = Request
            .Builder()
            .url("$baseUrl/rate-limiter/api/limit")
            .header("Accept", "application/json")
            .header("Authorization", "Bearer $idToken")
            .post(requestBody)
            .build()

        return try { fetch(request) } catch (e: IllegalStateException) { return true }
    }

    private inline fun <reified RESPONSE> fetch(request: Request): RESPONSE {
        val response: Response = client
            .newCall(request)
            .execute()

        val body = response.body()?.string()

        return if (response.code() in 200..299 && body != null) {
            objectMapper.readValue(body)
        } else {
            throw IllegalStateException("Forventet 200-range svar og body fra rate-limiter, men fikk: ${response.code()}")
        }
    }
}
