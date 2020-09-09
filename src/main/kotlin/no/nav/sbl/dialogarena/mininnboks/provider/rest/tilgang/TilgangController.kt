package no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import no.nav.common.auth.subject.SubjectHandler
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangDTO
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService

fun Route.tilgangController(tilgangService: TilgangService?) {

    route("/tilgang") {
    get("/oksos") {
            call.respond(SubjectHandler
                    .getIdent()
                    .map { fnr: String? -> tilgangService!!.harTilgangTilKommunalInnsending(fnr!!) }
                    .orElse(TilgangDTO(TilgangDTO.Resultat.FEILET, "Fant ikke brukers OIDC-token")))
        }
    }
}