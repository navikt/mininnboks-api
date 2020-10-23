package no.nav.sbl.dialogarena.mininnboks

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.utils.fn.UnsafeSupplier

suspend fun PipelineContext<Unit, ApplicationCall>.withSubject(block: suspend PipelineContext<Unit, ApplicationCall>.(Subject) -> Unit) {
    this.call.authentication.principal<SubjectPrincipal>()
            ?.subject
            ?.let { block(this, it) }
            ?: this.call.respond(HttpStatusCode.Forbidden, "Fant ikke subject")
}

suspend fun <T> externalCall(subject: Subject, block: () -> T): T = withContext(Dispatchers.IO) {
    SubjectHandler.withSubject(subject, UnsafeSupplier { block() })
}


fun Route.conditionalAuthenticate(useAuthentication: Boolean, build: Route.() -> Unit): Route {
    if (useAuthentication) {
        return authenticate(build = build)
    }
    val route = createChild(AuthenticationRouteSelector(listOf<String?>(null)))
    route.insertPhaseAfter(ApplicationCallPipeline.Features, Authentication.AuthenticatePhase)
    route.intercept(Authentication.AuthenticatePhase) {
        val subject = Subject("1234", IdentType.EksternBruker, SsoToken.oidcToken("3434", emptyMap<String, Any>()))
        this.context.authentication.principal = SubjectPrincipal(subject)
    }
    route.build()
    return route
}
