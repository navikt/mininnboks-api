package no.nav.sbl.dialogarena.mininnboks.common.okhttp

import no.nav.common.log.MDCConstants
import no.nav.common.utils.IdUtils
import no.nav.sbl.dialogarena.mininnboks.common.TjenestekallLogger
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import org.slf4j.LoggerFactory
import org.slf4j.MDC

class LoggingInterceptor : Interceptor {
    companion object {
        private val text = MediaType.get("text/plain")
        private val json = MediaType.get("application/json")
    }
    private val log = LoggerFactory.getLogger(LoggingInterceptor::class.java)
    private fun Request.peekContent(): String? {
        val contentType = this.body()?.contentType() ?: this.header("Content-Type")?.let(MediaType::parse)
        return when {
            contentType.equivalentTo(text, json) -> {
                val copy = this.newBuilder().build()
                val buffer = Buffer()
                copy.body()?.writeTo(buffer)
                buffer.readUtf8()
            }
            else -> "Content of type: $contentType"
        }
    }

    private fun Response.peekContent(): String? {
        val contentType = this.body()?.contentType() ?: this.header("Content-Type")?.let(MediaType::parse)
        return when {
            contentType.equivalentTo(text, json) -> this.peekBody(Long.MAX_VALUE).string().sanitize()
            else -> "Content of type: $contentType"
        }
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

private fun MediaType?.equivalentTo(vararg others: MediaType): Boolean {
    if (this == null) return false
    val thisType = "${this.type()}/${this.subtype()}"
    val otherTypes = others.map { "${it.type()}/${it.subtype()}" }
    return otherTypes.contains(thisType)
}

private fun String?.sanitize(): String? {
    return when {
        this == null -> null
        this.contains("token") -> this.scrambleWordsLongerThan(100)
        else -> this
    }
}

private fun String.scrambleWordsLongerThan(length: Int): String {
    val halfLength: Int = length / 2
    val words = this.split(" ")
    return words.joinToString(" ") { word ->
        if (word.length < length) {
            word
        } else {
            word.substring(0, halfLength) + "[...]" + word.substring(word.length - halfLength)
        }
    }
}
