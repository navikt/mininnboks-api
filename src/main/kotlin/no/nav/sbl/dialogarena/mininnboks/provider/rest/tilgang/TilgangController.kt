package no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.sbl.dialogarena.mininnboks.conditionalAuthenticate
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService
import no.nav.sbl.dialogarena.mininnboks.withSubject

fun Route.tilgangController(tilgangService: TilgangService, useAuthentication: Boolean) {

    conditionalAuthenticate(useAuthentication) {

        route("/tilgang") {
            get("/oksos") {
                withSubject { subject ->
                    call.respond(tilgangService.harTilgangTilKommunalInnsending(subject))
                }

            }
        }
    }
}
