package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangDTO
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.conditionalAuthenticate
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.getIdentifikator
import org.apache.cxf.binding.soap.SoapFault
import java.util.*
import javax.ws.rs.BadRequestException
import javax.ws.rs.NotAuthorizedException


fun Route.HenvendelseController(henvendelseService: HenvendelseService, tilgangService: TilgangService, useAuthentication: Boolean) {

    conditionalAuthenticate(useAuthentication) {
        route("/traader") {

            get("/") {
                val fnr = call.getIdentifikator() ?: throw  NotAuthorizedException("Fant ikke brukers OIDC-token")
                val henvendelser = henvendelseService.hentAlleHenvendelser(fnr)
                val traader = henvendelser!!.groupBy { it?.traadId }
                call.respond(traader.values
                        .map { t: List<Henvendelse?>? -> filtrerDelsvar(t) }
                        .map { meldinger: List<Henvendelse?>? -> Traad(meldinger) }
                        .sortedWith(Traad.NYESTE_FORST))
            }

            get("/{id}") {
                val id = call.parameters.get("id")
                val optionalTraad = hentTraad(henvendelseService, id)
                if (optionalTraad.isPresent) {
                    val traad = optionalTraad.get()
                    val filtrertTraad = Traad(filtrerDelsvar(traad.meldinger))
                    call.respond(filtrertTraad)
                } else {
                    call.respond(HttpStatusCode.Companion.NotFound)
                }
            }

            post("/lest/{behandlingsId}") {
                val behandlingsId = call.parameters.get("behandlingsId")

                if (behandlingsId != null) {
                    henvendelseService.merkSomLest(behandlingsId)
                    call.respond(TupleResultat.of("traadId", behandlingsId))
                }
            }

            post("/allelest/{behandlingskjedeId}") {
                var behandlingskjedeId = call.parameters.get("behandlingskjedeId")
                henvendelseService.merkAlleSomLest(behandlingskjedeId)
                call.respond(TupleResultat.of("traadId", behandlingskjedeId))
            }

            post("/sporsmal") {
                val fnr = call.getIdentifikator() ?: throw  NotAuthorizedException("Fant ikke brukers OIDC-token")
                val sporsmal = call.receive(Sporsmal::class)
                val henvendelse = lagHenvendelse(tilgangService, fnr, sporsmal)
                //  val metrikk = MetricsClient.createEvent("mininnboks.sendsporsmal")
                //  metrikk.addTagToReport("tema", sporsmal.temagruppe)
                // metrikk.report()
                val response = henvendelseService!!.stillSporsmal(henvendelse, fnr)
                call.respond(HttpStatusCode.Created, NyHenvendelseResultat(response.behandlingsId))
            }

            post("/sporsmaldirekte") {
                val fnr = call.getIdentifikator() ?: throw  NotAuthorizedException("Fant ikke brukers OIDC-token")
                val sporsmal = call.receive(Sporsmal::class)
                val henvendelse = lagHenvendelse(tilgangService, fnr, sporsmal)
                //val metrikk = MetricsFactory.createEvent("mininnboks.sendsporsmaldirekte")
                //metrikk.addTagToReport("tema", sporsmal.temagruppe)
                //metrikk.report()
                val response = henvendelseService!!.stillSporsmalDirekte(henvendelse, fnr)
                call.respond(HttpStatusCode.Created, NyHenvendelseResultat(response.behandlingsId))
            }

            post("/svar") {
                val svar = call.receive(Svar::class)
                assertFritekst(svar.fritekst)
                val traadOptional = hentTraad(henvendelseService, svar.traadId)
                if (!traadOptional.isPresent) {
                    call.respond(HttpStatusCode.NotFound)
                }
                val traad = traadOptional.get()
                if (!traad.kanBesvares) {
                    call.respond(HttpStatusCode.NotAcceptable)
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
                //val metrikk = MetricsFactory.createEvent("mininnboks.sendsvar")
                //metrikk.addTagToReport("tema", traad.nyeste?.temaKode)
                //metrikk.report()
                val fnr = call.getIdentifikator() ?: throw  NotAuthorizedException("Fant ikke brukers OIDC-token")
                val response = henvendelseService!!.sendSvar(henvendelse, fnr)
                call.respond(HttpStatusCode.Created, NyHenvendelseResultat(response!!.behandlingsId))
            }


        }

    }
}

fun lagHenvendelse(tilgangService: TilgangService, fnr: String, sporsmal: Sporsmal): Henvendelse {
    val temagruppe = sporsmal.temagruppe?.let { Temagruppe.valueOf(it) }
    assertFritekst(sporsmal.fritekst)
    if (temagruppe != null) {
        assertTemagruppeTilgang(tilgangService, fnr, temagruppe)
    }
    return Henvendelse(sporsmal.fritekst, temagruppe)
}

fun assertTemagruppeTilgang(tilgangService: TilgangService, fnr: String, temagruppe: Temagruppe) {
    if (!Temagruppe.GODKJENTE_FOR_INNGAAENDE_SPORSMAAL.contains(temagruppe)) {
        throw BadRequestException("Innsending på temagruppe er ikke godkjent")
    }
    if (temagruppe == Temagruppe.OKSOS && !harTilgangTilKommunalInnsending(tilgangService, fnr)) {
        throw BadRequestException("Bruker har ikke lov til å sende inn henvendelse på temagruppe OKSOS.")
    }
}

fun harTilgangTilKommunalInnsending(tilgangService: TilgangService, fnr: String): Boolean {
    return TilgangDTO.Resultat.OK == tilgangService!!.harTilgangTilKommunalInnsending(fnr).resultat
}

fun filtrerDelsvar(traad: List<Henvendelse?>?): List<Henvendelse?>? {
    if (traadHarIkkeSkriftligSvarFraNAV(traad)) {
        return traad?.sortedBy { henvendelse -> henvendelse?.type != Henvendelsetype.DELVIS_SVAR_SKRIFTLIG }
    } else
        return traad
}


fun traadHarIkkeSkriftligSvarFraNAV(traad: List<Henvendelse?>?): Boolean {
    val skriftligSvarFraNAV = traad?.stream()
            ?.filter { henvendelse -> henvendelse?.type == Henvendelsetype.SVAR_SKRIFTLIG }
            ?.findAny()
    return !skriftligSvarFraNAV?.isPresent!!
}

fun hentTraad(henvendelseService: HenvendelseService, id: String?): Optional<Traad> {
    return try {
        val meldinger = henvendelseService.hentTraad(id)
        if (meldinger == null || meldinger.isEmpty()) {
            Optional.empty()
        } else {
            Optional.of(Traad(meldinger))
        }
    } catch (fault: SoapFault) {
       //TODO( logger.error("Fant ikke tråd med id: $id", fault)
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


fun assertFritekst(fritekst: String?) {
    if (fritekst == null) {
        throw BadRequestException("Fritekst må være sendt med")
    } else if (fritekst.trim { it <= ' ' }.length == 0) {
        throw BadRequestException("Fritekst må inneholde tekst")
    } else if (fritekst.trim { it <= ' ' }.length > 1000) {
        throw BadRequestException("Fritekst kan ikke være lengre enn 1000 tegn")
    }
}




