package no.nav.sbl.dialogarena.mininnboks.provider.rest.resources

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstService
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstServiceImpl
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe
import no.nav.sbl.dialogarena.mininnboks.provider.LinkService

fun Route.resourcesController() {

    route("/resources") {

        get {

            val tekster: MutableMap<String?, Any?> = HashMap()
            TekstServiceImpl.hentTekster()?.let { it1 -> tekster.putAll(it1) }
            tekster["skriv.ny.link"] = LinkService.TEMAVELGER_LINK
            tekster["brukerprofil.link"] = LinkService.BRUKERPROFIL_LINK
            tekster["saksoversikt.link"] = LinkService.SAKSOVERSIKT_LINK
            tekster["temagruppe.liste"] = Temagruppe.GODKJENTE_FOR_INNGAAENDE_SPORSMAAL.joinToString( " ")
            call.respond(tekster)
        }
    }
}
