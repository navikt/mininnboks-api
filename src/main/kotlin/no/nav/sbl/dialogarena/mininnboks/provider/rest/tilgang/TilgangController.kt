package no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.sbl.dialogarena.mininnboks.AuthLevel
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService
import no.nav.sbl.dialogarena.mininnboks.withSubject

fun Route.tilgangController(tilgangService: TilgangService) {
    route("/tilgang") {
        get("/oksos") {
            withSubject(AuthLevel.Level4) { subject ->
                call.respond(tilgangService.harTilgangTilKommunalInnsending(subject))
            }
        }

        get("/folkeregistrertadresse") {
            withSubject(AuthLevel.Level4) { subject ->
                call.respond(tilgangService.hentFolkeregistrertAdresseMedGt(subject))
            }
        }
    }
}
