package no.nav.sbl.dialogarena.mininnboks

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.utils.fn.UnsafeSupplier

object KtorUtils {
    val authLevel = "Level4"
    val claims = mapOf(
            "acr" to authLevel
    )

    fun dummySubject(): Subject {
        return Subject("12345678910", IdentType.EksternBruker, SsoToken.oidcToken("3434", claims))
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.withSubject(block: suspend PipelineContext<Unit, ApplicationCall>.(Subject) -> Unit) =
        this.call.authentication.principal<SubjectPrincipal>()
                ?.subject
                ?.let {
                    if (it.ssoToken.attributes["acr"]?.equals(KtorUtils.authLevel)!!) {
                        block(this, it)
                    }
                }
                ?: this.call.respond(HttpStatusCode.Forbidden, "Fant ikke subject")

suspend fun <T> externalCall(subject: Subject, block: () -> T): T = withContext(Dispatchers.IO) {
    SubjectHandler.withSubject(subject, UnsafeSupplier { block() })
}


@KtorExperimentalAPI
fun Route.conditionalAuthenticate(useAuthentication: Boolean, build: Route.() -> Unit): Route {
    if (useAuthentication) {
        return authenticate(build = build)
    }
    val route = createChild(AuthenticationRouteSelector(listOf<String?>(null)))
    route.insertPhaseAfter(ApplicationCallPipeline.Features, Authentication.AuthenticatePhase)
    route.intercept(Authentication.AuthenticatePhase) {
        this.context.authentication.principal = SubjectPrincipal(KtorUtils.dummySubject())
    }
    route.build()
    return route
}


