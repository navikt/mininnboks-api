package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import no.nav.common.auth.SubjectHandler
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import java.util.function.Supplier
import javax.ws.rs.ForbiddenException

fun Route.SporsmalController(henvendelseService: HenvendelseService) {


    route("sporsmal") {

        get("/ubehandlet") {
            call.run {
                //TODO:Remove null
                respond(SubjectHandler.getIdent()
                        .map { fodselsnummer: String? -> henvendelseService.hentAlleHenvendelser(fodselsnummer) }
                        .map { henvendelser: List<Henvendelse?>? ->
                            if (henvendelser != null) {
                                UbehandletMeldingUtils.hentUbehandledeMeldinger(henvendelser)
                            }
                        }
                        .orElseThrow(Supplier { ForbiddenException("Fant ikke subjecthandler-ident") }))
            }
        }
    }
}
