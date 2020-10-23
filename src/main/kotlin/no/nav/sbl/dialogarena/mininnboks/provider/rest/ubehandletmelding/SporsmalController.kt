package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.sbl.dialogarena.mininnboks.conditionalAuthenticate
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.withSubject

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

