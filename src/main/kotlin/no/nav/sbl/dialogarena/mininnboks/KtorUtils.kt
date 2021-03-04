package no.nav.sbl.dialogarena.mininnboks

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import no.nav.common.auth.subject.Subject
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.utils.fn.UnsafeSupplier

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

suspend fun <T> externalCall(subject: Subject, block: () -> T): T = withContext(Dispatchers.IO + MDCContext()) {
    SubjectHandler.withSubject(subject, UnsafeSupplier { block() })
}
