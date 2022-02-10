package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.common.auth.subject.Subject
import no.nav.sbl.dialogarena.mininnboks.AuthLevel
import no.nav.sbl.dialogarena.mininnboks.common.audit.Audit.Action.READ
import no.nav.sbl.dialogarena.mininnboks.common.audit.Audit.Action.UPDATE
import no.nav.sbl.dialogarena.mininnboks.common.audit.Audit.Companion.describe
import no.nav.sbl.dialogarena.mininnboks.common.audit.Audit.Companion.withAudit
import no.nav.sbl.dialogarena.mininnboks.common.audit.AuditIdentifier.BEHANDLINGSID
import no.nav.sbl.dialogarena.mininnboks.common.audit.AuditIdentifier.BEHANDLINGSKJEDEID
import no.nav.sbl.dialogarena.mininnboks.common.audit.AuditResources.Companion.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.common.audit.AuditResources.Companion.Les
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Traad
import no.nav.sbl.dialogarena.mininnboks.withSubject
import org.apache.cxf.binding.soap.SoapFault
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.xml.ws.soap.SOAPFaultException

val logger: Logger = LoggerFactory.getLogger("mininnboks.henvendelseController")

fun Route.henvendelseController(
    henvendelseService: HenvendelseService
) {
    route("/traader") {
        hentAlleTraader(henvendelseService)
        getId(henvendelseService)
        postByBehandlingsId(henvendelseService)
        alleLest(henvendelseService)
    }
}

private fun Route.hentAlleTraader(henvendelseService: HenvendelseService) {
    get("/") {
        withSubject(AuthLevel.Level4) { subject ->
            withAudit(describe(subject, READ, Henvendelse)) {
                val henvendelser: List<Henvendelse> = henvendelseService.hentAlleHenvendelser(subject)
                val traader: Map<String?, List<Henvendelse>> = henvendelser.groupBy { it.traadId }
                call.respond(
                    traader.values
                        .map { meldinger: List<Henvendelse> -> Traad(meldinger) }
                        .sortedWith(Traad.NYESTE_FORST)
                )
            }
        }
    }
}

private fun Route.alleLest(henvendelseService: HenvendelseService) {
    post("/allelest/{behandlingskjedeId}") {
        val behandlingskjedeId = call.parameters["behandlingskjedeId"]
        withSubject(AuthLevel.Level4) { subject ->
            withAudit(describe(subject, UPDATE, Les, BEHANDLINGSKJEDEID to call.parameters["behandlingskjedeId"])) {
                if (behandlingskjedeId != null) {
                    henvendelseService.merkAlleSomLest(behandlingskjedeId, subject)
                    call.respond(mutableMapOf("traadId" to behandlingskjedeId))
                }
            }
        }
    }
}

private fun Route.postByBehandlingsId(henvendelseService: HenvendelseService) {
    post("/lest/{behandlingsId}") {
        val behandlingsId = call.parameters["behandlingsId"]

        withSubject(AuthLevel.Level4) { subject ->
            withAudit(describe(subject, UPDATE, Les, BEHANDLINGSID to call.parameters["behandlingsId"])) {
                if (behandlingsId != null) {
                    henvendelseService.merkSomLest(behandlingsId, subject)
                    call.respond(mapOf("traadId" to behandlingsId))
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
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
            withAudit(describe(subject, READ, Henvendelse, BEHANDLINGSID to id)) {
                if (id != null) {
                    val traad = hentTraad(henvendelseService, id, subject)

                    if (traad != null) {
                        call.respond(traad)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
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
