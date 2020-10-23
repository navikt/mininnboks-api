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
import no.nav.sbl.dialogarena.mininnboks.KtorUtils.authLevel3
import no.nav.sbl.dialogarena.mininnboks.KtorUtils.authLevel4

object KtorUtils {
    const val authLevel4 = "Level4"
    const val authLevel3 = "Level3"

    fun dummySubject(): Subject {
        return Subject("12345678910", IdentType.EksternBruker, SsoToken.oidcToken("3434", emptyMap<String, Object>()))
    }
}

suspend fun PipelineContext<Unit, ApplicationCall>.withSubject(block: suspend PipelineContext<Unit, ApplicationCall>.(Subject) -> Unit) =
        this.call.authentication.principal<SubjectPrincipal>()
                ?.takeIf {
                    it.authLevel == authLevel4 || (it.authLevel == authLevel3 && this.call.request.url().contains("sporsmal/ubehandlet"))
                }
                ?.subject
                ?.let {
                    block(this, it)
                }
                ?: this.call.respond(HttpStatusCode.Forbidden, "Fant ikke subject ellers authLevel ikke på nivå $authLevel4")

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
        this.context.authentication.principal = SubjectPrincipal(KtorUtils.dummySubject(), authLevel4)
    }
    route.build()
    return route
}


