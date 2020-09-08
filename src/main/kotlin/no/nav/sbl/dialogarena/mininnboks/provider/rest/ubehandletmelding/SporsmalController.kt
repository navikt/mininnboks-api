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
import no.nav.sbl.dialogarena.mininnboks.MockPayload
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import javax.ws.rs.ForbiddenException

fun Route.conditionalAuthenticate(useAuthentication: Boolean, build: Route.() -> Unit): Route {
    if (useAuthentication) {
        return authenticate(build = build)
    }
    val route = createChild(AuthenticationRouteSelector(listOf<String?>(null)))
    route.insertPhaseAfter(ApplicationCallPipeline.Features, Authentication.AuthenticatePhase)
    route.intercept(Authentication.AuthenticatePhase) {
        this.context.authentication.principal = JWTPrincipal(MockPayload("12345678910"))
    }
    route.build()
    return route
}


fun Route.sporsmalController(henvendelseService: HenvendelseService, useAuthentication: Boolean) {

    conditionalAuthenticate(useAuthentication) {
        route("/sporsmal") {

            get("/ubehandlet") {
                val fnr = call.getIdentifikator()
                call.respond(
                        henvendelseService.hentAlleHenvendelser(fnr)
                                ?.map { UbehandletMeldingUtils::hentUbehandledeMeldinger }
                                ?: throw ForbiddenException("Fant ikke subjecthandler-ident"))

            }
        }
    }
}

fun ApplicationCall.getIdentifikator(): String? {
    return this.principal<JWTPrincipal>()
            ?.payload
            ?.subject
}

