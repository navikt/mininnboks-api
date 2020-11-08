package no.nav.sbl.dialogarena.mininnboks

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import io.ktor.util.*
import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject

@KtorExperimentalAPI
fun Route.conditionalAuthenticate(subjectPrincipal: SubjectPrincipal, build: Route.() -> Unit): Route {
    val route = createChild(AuthenticationRouteSelector(listOf<String?>(null)))
    route.insertPhaseAfter(ApplicationCallPipeline.Features, Authentication.AuthenticatePhase)
    route.intercept(Authentication.AuthenticatePhase) {
        this.context.authentication.principal = subjectPrincipal
    }
    route.build()
    return route
}

val dummySubject = Subject("12345678910", IdentType.EksternBruker, SsoToken.oidcToken("3434", emptyMap<String, Any>()))

fun dummyPrincipalNiva3(): SubjectPrincipal {
    return SubjectPrincipal(dummySubject, AuthLevel.Level3.name)
}

fun dummyPrincipalNiva4(): SubjectPrincipal {
    return SubjectPrincipal(dummySubject, AuthLevel.Level4.name)
}
