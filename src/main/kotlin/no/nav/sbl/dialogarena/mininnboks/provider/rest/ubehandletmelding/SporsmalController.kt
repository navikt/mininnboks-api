package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.sbl.dialogarena.mininnboks.AuthLevel
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.withSubject

fun Route.sporsmalController(henvendelseService: HenvendelseService) {
    route("/sporsmal") {
        get("/ubehandlet") {
            withSubject(AuthLevel.Level3) { subject ->
                call.respond(
                    UbehandletMeldingUtils.hentUbehandledeMeldinger(henvendelseService.hentAlleHenvendelser(subject))
                )
            }
        }
    }
}
