package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.auth.*
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import no.nav.sbl.dialogarena.mininnboks.SubjectPrincipal
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.withSubject

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


fun Route.sporsmalController(henvendelseService: HenvendelseService, useAuthentication: Boolean) {

    conditionalAuthenticate(useAuthentication) {
        route("/sporsmal") {
            get("/ubehandlet") {
                withSubject { subject ->
                    call.respond(
                      UbehandletMeldingUtils.hentUbehandledeMeldinger(henvendelseService.hentAlleHenvendelser(subject))
                    )
                }
            }
        }
    }
}

fun ApplicationCall.getIdentifikator(): Subject? {
    return this.principal<SubjectPrincipal>()?.subject
}
