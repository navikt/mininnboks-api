package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.common.auth.subject.Subject
import no.nav.sbl.dialogarena.mininnboks.AuthLevel
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.RateLimiterGateway
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangDTO
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService
import no.nav.sbl.dialogarena.mininnboks.withSubject
import org.apache.cxf.binding.soap.SoapFault
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.xml.ws.soap.SOAPFaultException

val logger: Logger = LoggerFactory.getLogger("mininnboks.henvendelseController")

fun Route.henvendelseController(
    henvendelseService: HenvendelseService,
    tilgangService: TilgangService,
    rateLimiterGateway: RateLimiterGateway
) {
    route("/traader") {
        hentAlleTraader(henvendelseService)

        getId(henvendelseService)

        postByBehandlingsId(henvendelseService)

        alleLest(henvendelseService)

        sporsmal(tilgangService, henvendelseService, rateLimiterGateway)

        sporsmaldirekte(tilgangService, henvendelseService, rateLimiterGateway)

        svar(henvendelseService)
    }
}

private fun Route.hentAlleTraader(henvendelseService: HenvendelseService) {
    get("/") {
        withSubject(AuthLevel.Level4) { subject ->
            val henvendelser: List<Henvendelse> = henvendelseService.hentAlleHenvendelser(subject)
            val traader: Map<String?, List<Henvendelse>> = henvendelser.groupBy { it.traadId }
            call.respond(
                traader.values
                    .map { list: List<Henvendelse> -> filtrerDelsvar(list) }
                    .map { meldinger: List<Henvendelse> -> Traad(meldinger) }
                    .sortedWith(Traad.NYESTE_FORST)
            )
        }
    }
}

private fun Route.svar(henvendelseService: HenvendelseService) {
    post("/svar") {
        val svar = call.receive(Svar::class)
        assertFritekst(svar.fritekst, 2500)
        withSubject(AuthLevel.Level4) { subject ->
            val traad = hentTraad(henvendelseService, svar.traadId, subject)
            if (traad == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                if (!traad.kanBesvares) {
                    call.respond(HttpStatusCode.NotAcceptable)
                    return@withSubject
                } else {
                    val henvendelse = createHenvendelse(svar, traad)
                    val response = henvendelseService.sendSvar(henvendelse, subject)
                    call.respond(HttpStatusCode.Created, NyHenvendelseResultat(response.behandlingsId))
                }
            }
        }
    }
}

private fun createHenvendelse(svar: Svar, traad: Traad): Henvendelse {
    return Henvendelse(
        fritekst = svar.fritekst,
        temagruppe = traad.nyeste?.temagruppe,
        traadId = svar.traadId,
        eksternAktor = traad.nyeste?.eksternAktor,
        brukersEnhet = traad.eldste?.brukersEnhet,
        tilknyttetEnhet = traad.nyeste?.tilknyttetEnhet,
        type = Henvendelsetype.SVAR_SBL_INNGAAENDE,
        opprettet = Date(),
        lestDato = Date(),
        erTilknyttetAnsatt = traad.nyeste?.erTilknyttetAnsatt,
        kontorsperreEnhet = traad.nyeste?.kontorsperreEnhet
    )
}

private fun Route.sporsmaldirekte(
    tilgangService: TilgangService,
    henvendelseService: HenvendelseService,
    rateLimiterGateway: RateLimiterGateway
) {
    post("/sporsmaldirekte") {
        withSubject(AuthLevel.Level4) { subject ->
            takeIf {
                rateLimiterGateway.erOkMedSendeSpørsmål()
            }?.let {
                call.receive(Sporsmal::class).let {
                    lagHenvendelse(tilgangService, subject, it).let {
                        henvendelseService.stillSporsmalDirekte(it, subject).also {
                            call.respond(HttpStatusCode.Created, NyHenvendelseResultat(it.behandlingsId))
                            rateLimiterGateway.oppdatereRateLimiter()
                        }
                    }
                }
            } ?: call.respond(HttpStatusCode.NotAcceptable, "Maks grense for å sende spørsmålet er nådd")
        }
    }
}

private fun Route.sporsmal(
    tilgangService: TilgangService,
    henvendelseService: HenvendelseService,
    rateLimiterGateway: RateLimiterGateway
) {
    post("/sporsmal") {
        withSubject(AuthLevel.Level4) { subject ->
            takeIf {
                rateLimiterGateway.erOkMedSendeSpørsmål()
            }?.let {
                call.receive(Sporsmal::class).let { sporsmal ->
                    lagHenvendelse(tilgangService, subject, sporsmal).let {
                        henvendelseService.stillSporsmal(it, sporsmal.overstyrtGt, subject).also {
                            call.respond(HttpStatusCode.Created, NyHenvendelseResultat(it.behandlingsId))
                            rateLimiterGateway.oppdatereRateLimiter()
                        }
                    }
                }
            } ?: call.respond(HttpStatusCode.NotAcceptable, "Maks grense for å sende spørsmålet er nådd")
        }
    }
}

private fun Route.alleLest(henvendelseService: HenvendelseService) {
    post("/allelest/{behandlingskjedeId}") {
        val behandlingskjedeId = call.parameters["behandlingskjedeId"]

        withSubject(AuthLevel.Level4) { subject ->
            if (behandlingskjedeId != null) {
                henvendelseService.merkAlleSomLest(behandlingskjedeId, subject)
                call.respond(mutableMapOf("traadId" to behandlingskjedeId))
            }
        }
    }
}

private fun Route.postByBehandlingsId(henvendelseService: HenvendelseService) {
    post("/lest/{behandlingsId}") {
        val behandlingsId = call.parameters["behandlingsId"]

        if (behandlingsId != null) {
            withSubject(AuthLevel.Level4) { subject ->
                henvendelseService.merkSomLest(behandlingsId, subject)
                call.respond(mapOf("traadId" to behandlingsId))
            }
        } else {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}

private fun Route.getId(henvendelseService: HenvendelseService) {
    get("/{id}") {
        val id = call.parameters["id"]
        if (id == null) {
            call.respond(HttpStatusCode.NotFound)
        }
        withSubject(AuthLevel.Level4) { subject ->
            if (id != null) {
                val traad = hentTraad(henvendelseService, id, subject)

                if (traad != null) {
                    val filtrertTraad = Traad(filtrerDelsvar(traad.meldinger))
                    call.respond(filtrertTraad)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}

suspend fun lagHenvendelse(tilgangService: TilgangService, subject: Subject, sporsmal: Sporsmal): Henvendelse {
    assertFritekst(sporsmal.fritekst)
    assertOverstyrtGt(sporsmal)
    assertTemagruppeTilgang(tilgangService, subject, sporsmal.temagruppe)
    return Henvendelse(fritekst = sporsmal.fritekst, temagruppe = sporsmal.temagruppe, type = Henvendelsetype.SPORSMAL_SKRIFTLIG)
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
    } else {
        traad
    }
}

fun traadHarIkkeSkriftligSvarFraNAV(traad: List<Henvendelse?>?): Boolean {
    return traad?.filter { henvendelse -> henvendelse?.type == Henvendelsetype.SVAR_SKRIFTLIG }.isNullOrEmpty()
}

suspend fun hentTraad(henvendelseService: HenvendelseService, id: String, subject: Subject): Traad? {
    return try {
        val hevendelser = henvendelseService.hentTraad(id, subject)
        if (hevendelser.isNotEmpty()) {
            Traad(hevendelser)
        } else {
            null
        }
    } catch (fault: SoapFault) {
        logger.error("Fant ikke tråd med id: $id", fault)
        null
    } catch (fault1: SOAPFaultException) {
        logger.error("Fant ikke tråd med id: $id", fault1)
        null
    } catch (e: Exception) {
        logger.error("Uforventet feil $e.message")
        null
    }
}

class NyHenvendelseResultat(val behandlingsId: String?)

fun assertFritekst(fritekst: String) {
    assertFritekst(fritekst, 1000)
}

fun assertOverstyrtGt(sporsmal: Sporsmal) {
    if (sporsmal.temagruppe == Temagruppe.OKSOS && sporsmal.overstyrtGt.isNullOrBlank()) {
        throw BadRequestException("overstyrtGT må være definert ved innsending på OKSOS")
    } else if (sporsmal.temagruppe != Temagruppe.OKSOS && !sporsmal.overstyrtGt.isNullOrBlank()) {
        throw BadRequestException("overstyrtGT må være udefinert ved innsending på ${sporsmal.temagruppe}")
    }
}

fun assertFritekst(fritekst: String?, maxLengde: Int) {
    when {
        fritekst == null -> {
            throw BadRequestException("Fritekst må være sendt med")
        }
        fritekst.trim().isEmpty() -> {
            throw BadRequestException("Fritekst må inneholde tekst")
        }
        fritekst.trim().length > maxLengde -> {
            throw BadRequestException("Fritekst kan ikke være lengre enn $maxLengde tegn")
        }
    }
}
