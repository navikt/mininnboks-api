package no.nav.sbl.dialogarena.mininnboks

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import no.nav.common.auth.subject.Subject
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.utils.fn.UnsafeSupplier
import no.nav.sbl.dialogarena.mininnboks.common.okhttp.LoggingInterceptor

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

fun createHttpClient(name: String?) = HttpClient(OkHttp) {
    if (name != null) {
        engine {
            addInterceptor(LoggingInterceptor(name))
        }
    }
    install(JsonFeature) {
        serializer = JacksonSerializer(JacksonUtils.objectMapper)
    }
}
