package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse

import io.ktor.application.call
import io.ktor.features.BadRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import no.nav.common.auth.subject.Subject
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangDTO
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.conditionalAuthenticate
import no.nav.sbl.dialogarena.mininnboks.withSubject
import org.apache.cxf.binding.soap.SoapFault
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.xml.ws.soap.SOAPFaultException

val logger: Logger = LoggerFactory.getLogger("mininnboks.henvendelseController")


fun Route.henvendelseController(henvendelseService: HenvendelseService, tilgangService: TilgangService, useAuthentication: Boolean) {

    conditionalAuthenticate(useAuthentication) {
        route("/traader") {

            get("/") {
                withSubject { subject ->
                    val henvendelser: List<Henvendelse> = henvendelseService.hentAlleHenvendelser(subject)
                    val traader: Map<String?, List<Henvendelse>> = henvendelser.groupBy { it.traadId }
                    call.respond(traader.values
                            .map { list: List<Henvendelse> -> filtrerDelsvar(list) }
                            .map { meldinger: List<Henvendelse> -> Traad(meldinger) }
                            .sortedWith(Traad.NYESTE_FORST))
                }
            }

            get("/{id}") {
                val id = call.parameters["id"]
                withSubject { subject ->
                    val optionalTraad = hentTraad(henvendelseService, id, subject)

                    if (optionalTraad.isPresent) {
                        val traad = optionalTraad.get()
                        val filtrertTraad = Traad(filtrerDelsvar(traad.meldinger))
                        call.respond(filtrertTraad)
                    } else {
                        call.respond(HttpStatusCode.Companion.NotFound)
                    }
                }
            }

            post("/lest/{behandlingsId}") {
                val behandlingsId = call.parameters["behandlingsId"]

                if (behandlingsId != null) {
                    withSubject() { subject ->
                        henvendelseService.merkSomLest(behandlingsId, subject)
                        call.respond(TupleResultat.of("traadId", behandlingsId))
                    }

                }
            }

            post("/allelest/{behandlingskjedeId}") {
                withSubject { subject ->
                    val behandlingskjedeId = call.parameters["behandlingskjedeId"]
                    henvendelseService.merkAlleSomLest(behandlingskjedeId, subject)
                    call.respond(TupleResultat.of("traadId", behandlingskjedeId))
                }
            }

            post("/sporsmal") {
                withSubject { subject ->

                    val sporsmal = call.receive(Sporsmal::class)
                    val henvendelse = lagHenvendelse(tilgangService, subject, sporsmal)
                    val response = henvendelseService.stillSporsmal(henvendelse, subject)
                    call.respond(HttpStatusCode.Created, NyHenvendelseResultat(response.behandlingsId))
                }
            }

            post("/sporsmaldirekte") {
                withSubject { subject ->
                    val sporsmal = call.receive(Sporsmal::class)
                    val henvendelse = lagHenvendelse(tilgangService, subject, sporsmal)
                    val response = henvendelseService.stillSporsmalDirekte(henvendelse, subject)
                    call.respond(HttpStatusCode.Created, NyHenvendelseResultat(response.behandlingsId))
                }
            }

            post("/svar") {
                val svar = call.receive(Svar::class)
                assertFritekst(svar.fritekst, 2500)
                withSubject { subject ->
                    val traadOptional = hentTraad(henvendelseService, svar.traadId, subject)
                    if (!traadOptional.isPresent) {
                        call.respond(HttpStatusCode.NotFound)
                    } else {
                        val traad = traadOptional.get()
                        if (!traad.kanBesvares) {
                            call.respond(HttpStatusCode.NotAcceptable)
                            return@withSubject
                        }
                        val henvendelse = Henvendelse(svar.fritekst, traad.nyeste?.temagruppe)
                        henvendelse.traadId = svar.traadId
                        henvendelse.eksternAktor = traad.nyeste?.eksternAktor
                        henvendelse.brukersEnhet = traad.eldste?.brukersEnhet
                        henvendelse.tilknyttetEnhet = traad.nyeste?.tilknyttetEnhet
                        henvendelse.type = Henvendelsetype.SVAR_SBL_INNGAAENDE
                        henvendelse.opprettet = Date()
                        henvendelse.markerSomLest()
                        henvendelse.erTilknyttetAnsatt = traad.nyeste?.erTilknyttetAnsatt
                        henvendelse.kontorsperreEnhet = traad.nyeste?.kontorsperreEnhet
                        val response = henvendelseService.sendSvar(henvendelse, subject)
                        call.respond(HttpStatusCode.Created, NyHenvendelseResultat(response!!.behandlingsId))
                    }

                }
            }
        }

    }
}

suspend fun lagHenvendelse(tilgangService: TilgangService, subject: Subject, sporsmal: Sporsmal): Henvendelse {
    val temagruppe = sporsmal.temagruppe?.let { Temagruppe.valueOf(it) }
    sporsmal.fritekst?.let { assertFritekst(it) }
    if (temagruppe != null) {
        assertTemagruppeTilgang(tilgangService, subject, temagruppe)
    }
    return Henvendelse(sporsmal.fritekst, temagruppe)
}

suspend fun assertTemagruppeTilgang(tilgangService: TilgangService, subject: Subject, temagruppe: Temagruppe) {
    if (!Temagruppe.GODKJENTE_FOR_INNGAAENDE_SPORSMAAL.contains(temagruppe)) {
        throw BadRequestException("Innsending på temagruppe er ikke godkjent")
    }
    if (temagruppe == Temagruppe.OKSOS && !harTilgangTilKommunalInnsending(tilgangService, subject)) {
        throw BadRequestException("Bruker har ikke lov til å sende inn henvendelse på temagruppe OKSOS.")
    }
}

suspend fun harTilgangTilKommunalInnsending(tilgangService: TilgangService, subject: Subject): Boolean {
    return TilgangDTO.Resultat.OK == tilgangService.harTilgangTilKommunalInnsending(subject).resultat
}

fun filtrerDelsvar(traad: List<Henvendelse>): List<Henvendelse> {
    return if (traadHarIkkeSkriftligSvarFraNAV(traad)) {
        traad.filter { henvendelse -> henvendelse.type != Henvendelsetype.DELVIS_SVAR_SKRIFTLIG }
    } else
        traad
}


fun traadHarIkkeSkriftligSvarFraNAV(traad: List<Henvendelse?>?): Boolean {
    val skriftligSvarFraNAV = traad?.stream()
            ?.filter { henvendelse -> henvendelse?.type == Henvendelsetype.SVAR_SKRIFTLIG }
            ?.findAny()
    return !skriftligSvarFraNAV?.isPresent!!
}

suspend fun hentTraad(henvendelseService: HenvendelseService, id: String?, subject: Subject): Optional<Traad> {
    return try {

        val meldinger = henvendelseService.hentTraad(id, subject)
        if (meldinger.isEmpty()) {
            Optional.empty()
        } else Optional.of(Traad(meldinger))
    } catch (fault: SoapFault) {
        logger.error("Fant ikke tråd med id: $id", fault)
        Optional.empty()

    } catch (fault1: SOAPFaultException) {
        logger.error("Fant ikke tråd med id: $id", fault1)
        Optional.empty()
    }
}


class NyHenvendelseResultat(val behandlingsId: String?)

class TupleResultat : HashMap<String?, String?>() {
    companion object {
        fun of(key: String, value: String?): TupleResultat {
            val tr = TupleResultat()
            tr[key] = value
            return tr
        }
    }
}

fun assertFritekst(fritekst: String) {
    assertFritekst(fritekst, 1000)
}

fun assertFritekst(fritekst: String?, maxLengde: Int) {
    if (fritekst == null) {
        throw BadRequestException("Fritekst må være sendt med")
    } else if (fritekst.trim { it <= ' ' }.isEmpty()) {
        throw BadRequestException("Fritekst må inneholde tekst")
    } else if (fritekst.trim { it <= ' ' }.length > maxLengde) {
        throw BadRequestException("Fritekst kan ikke være lengre enn $maxLengde tegn")
    }
}





