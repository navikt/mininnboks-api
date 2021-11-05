package no.nav.sbl.dialogarena.mininnboks

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import no.nav.common.auth.subject.Subject
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.log.MDCConstants
import no.nav.common.utils.IdUtils
import no.nav.common.utils.fn.UnsafeSupplier
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.text.Charsets

enum class AuthLevel(val level: Int) {
    Level4(4),
    Level3(3)
}

suspend fun PipelineContext<Unit, ApplicationCall>.withSubject(authLevel: AuthLevel, block: suspend PipelineContext<Unit, ApplicationCall>.(Subject) -> Unit) =
    this.call.authentication.principal<SubjectPrincipal>()
        ?.takeIf {
            AuthLevel.valueOf(it.authLevel).level >= authLevel.level
        }
        ?.subject
        ?.let {
            block(this, it)
        }
        ?: this.call.respond(HttpStatusCode.Forbidden, "Fant ikke subject ellers authLevel ikke på nivå ${authLevel.name}")

suspend fun <T> externalCall(subject: Subject, block: suspend () -> T): T =
    withContext(Dispatchers.IO + MDCContext()) {
        SubjectHandler.withSubject(
            subject,
            UnsafeSupplier<T> {
                runBlocking {
                    block()
                }
            }
        )
    }

class TjenestekallLogging(val config: Config) {
    class Config {
        var logger: Logger = LoggerFactory.getLogger("SecureLog")
    }

    private suspend fun log(request: HttpRequest, response: HttpResponse) {
        val callId = MDC.putIfAbsent(MDCConstants.MDC_CALL_ID, IdUtils.generateId())
        val requestId = IdUtils.generateId()
        val requestLog = format(
            "Request $callId ($requestId)",
            mapOf(
                "url" to request.url,
                "headers" to request.headers.names().joinToString(", "),
                "body" to request.peekContent()
            )
        )

        if (response.status.isSuccess()) {
            config.logger.info(requestLog)
            config.logger.info(
                format(
                    "Response $callId ($requestId)",
                    mapOf(
                        "url" to request.url,
                        "headers" to request.headers.names().joinToString(", "),
                        "body" to response.peekContent()
                    )
                )
            )
        } else {
            config.logger.info(requestLog)
            config.logger.error(
                format(
                    "Response-Error $callId ($requestId)",
                    mapOf(
                        "url" to request.url,
                        "headers" to request.headers.names().joinToString(", "),
                        "body" to response.peekContent()
                    )
                )
            )
        }
    }

    private fun HttpRequest.peekContent(): String? {
        return when (val content = this.content) {
            is OutgoingContent.ByteArrayContent -> {
                String(content.bytes(), content.contentType?.charset() ?: Charsets.UTF_8)
            }
            else -> null
        }
    }

    private suspend fun HttpResponse.peekContent(): String {
        return this.content.readRemaining()
            .readText(charset = this.contentType()?.charset() ?: Charsets.UTF_8)
    }

    private fun format(header: String, fields: Map<String, Any?>): String {
        val sb = StringBuilder()
        sb.appendln(header)
        sb.appendln("------------------------------------------------------------------------------------")
        fields.forEach { (key, value) ->
            sb.appendln("$key: $value")
        }
        sb.appendln("------------------------------------------------------------------------------------")
        return sb.toString()
    }

    companion object : HttpClientFeature<Config, TjenestekallLogging> {
        private val log = LoggerFactory.getLogger(TjenestekallLogging::class.java)
        override val key: AttributeKey<TjenestekallLogging> = AttributeKey("TjenestekallLogging")
        override fun prepare(block: Config.() -> Unit): TjenestekallLogging {
            val config = Config().apply(block)
            return TjenestekallLogging(config)
        }

        override fun install(feature: TjenestekallLogging, scope: HttpClient) {
            scope.responsePipeline.intercept(HttpResponsePipeline.Receive) {
                try {
                    feature.log(context.request, context.response)
                } catch (e: Throwable) {
                    log.error("Error providing client log", e)
                }
                proceedWith(subject)
            }
        }
    }
}
