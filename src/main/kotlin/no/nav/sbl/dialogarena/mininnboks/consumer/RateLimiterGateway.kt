package no.nav.sbl.dialogarena.mininnboks.consumer

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.rest.client.RestClient
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.mininnboks.AuthorizationInterceptor
import no.nav.sbl.dialogarena.mininnboks.LoggingInterceptor
import no.nav.sbl.dialogarena.mininnboks.OkHttpUtils
import no.nav.sbl.dialogarena.mininnboks.XCorrelationIdInterceptor
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.slf4j.LoggerFactory

interface RateLimiterGateway {
    fun erOkMedSendeSpørsmål(): Boolean
    fun oppdatereRateLimiter(): Boolean
}

class RateLimiterGatewayImpl(
    private val baseUrl: String = EnvironmentUtils.getRequiredProperty("RATE_LIMITER_URL"),
    private val stsService: SystemuserTokenProvider

) : RateLimiterGateway {
    private val log = LoggerFactory.getLogger(RateLimiterGatewayImpl::class.java)
    private val objectMapper = OkHttpUtils.objectMapper
    private val client = RestClient.baseClient().newBuilder()
        .addInterceptor(XCorrelationIdInterceptor())
        .addInterceptor(
            AuthorizationInterceptor {
                stsService.getSystemUserAccessToken()!!
            }
        )
        .addInterceptor(
            LoggingInterceptor("rate-limiter") { request ->
                requireNotNull(request.header("X-Correlation-ID")) {
                    "Kall uten \"X-Correlation-ID\" er ikke lov"
                }
            }
        )
        .build()

    override fun erOkMedSendeSpørsmål(): Boolean {
        val request = Request
            .Builder()
            .url("$baseUrl/rate-limiter/api/limit")
            .header("accept", "application/json")
            .build()

        return fetch(request)
    }

    override fun oppdatereRateLimiter(): Boolean {
        val requestBody = RequestBody.create(
            OkHttpUtils.MediaTypes.JSON,
            objectMapper.writeValueAsString("")
        )
        val request = Request
            .Builder()
            .url("$baseUrl/rate-limiter/api/limit")
            .header("accept", "application/json")
            .post(requestBody)
            .build()

        return fetch(request)
    }

    private inline fun <reified RESPONSE> fetch(request: Request): RESPONSE {
        val response: Response = client
            .newCall(request)
            .execute()

        val body = response.body()?.string()

        return if (response.code() in 200..299 && body != null) {
            objectMapper.readValue(body)
        } else {
            log.error("Forventet 200-range svar og body fra rate-limiter, men fikk: ${response.code()}")
            objectMapper.readValue("true")
        }
    }
}
