package no.nav.sbl.dialogarena.mininnboks.common.okhttp

import no.nav.common.log.MDCConstants
import no.nav.common.utils.IdUtils
import no.nav.sbl.dialogarena.mininnboks.common.TjenestekallLogger
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.slf4j.LoggerFactory
import org.slf4j.MDC

class LoggingInterceptor() : Interceptor {
    private val log = LoggerFactory.getLogger(LoggingInterceptor::class.java)
    private fun Request.peekContent(): String? {
        val copy = this.newBuilder().build()
        val buffer = Buffer()
        copy.body()?.writeTo(buffer)

        return buffer.readUtf8()
    }

    private fun Response.peekContent(): String? {
        return this.peekBody(Long.MAX_VALUE).string()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val callId = MDC.get(MDCConstants.MDC_CALL_ID) ?: IdUtils.generateId()
        val requestId = IdUtils.generateId()
        val requestBody = request.peekContent()

        TjenestekallLogger.info(
            "Request: $callId ($requestId)",
            mapOf(
                "url" to request.url().toString(),
                "headers" to request.headers().names().joinToString(", "),
                "body" to requestBody
            )
        )

        val response: Response = runCatching { chain.proceed(request) }
            .onFailure { exception ->
                log.error("Response-error (ID: $callId)", exception)
                TjenestekallLogger.error(
                    "Response-error: $callId ($requestId)",
                    mapOf(
                        "exception" to exception
                    )
                )
            }
            .getOrThrow()

        val responseBody = response.peekContent()

        if (response.code() in 200..299) {
            TjenestekallLogger.info(
                "Response: $callId ($requestId)",
                mapOf(
                    "status" to "${response.code()} ${response.message()}",
                    "body" to responseBody
                )
            )
        } else {
            TjenestekallLogger.error(
                "Response-error: $callId ($requestId)",
                mapOf(
                    "status" to "${response.code()} ${response.message()}",
                    "request" to request,
                    "body" to responseBody
                )
            )
        }
        return response
    }
}
