package no.nav.sbl.dialogarena.mininnboks.provider.rest.dokument

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.sbl.dialogarena.mininnboks.AuthLevel
import no.nav.sbl.dialogarena.mininnboks.common.audit.Audit.Action.READ
import no.nav.sbl.dialogarena.mininnboks.common.audit.Audit.Companion.describe
import no.nav.sbl.dialogarena.mininnboks.common.audit.Audit.Companion.withAudit
import no.nav.sbl.dialogarena.mininnboks.common.audit.AuditIdentifier.DOKUMENT
import no.nav.sbl.dialogarena.mininnboks.common.audit.AuditIdentifier.JOURNALPOST
import no.nav.sbl.dialogarena.mininnboks.common.audit.AuditResources.Companion.Dokument
import no.nav.sbl.dialogarena.mininnboks.common.audit.AuditResources.Companion.Journalpost
import no.nav.sbl.dialogarena.mininnboks.consumer.saf.SafService
import no.nav.sbl.dialogarena.mininnboks.withSubject

fun Route.dokumentController(safService: SafService) {
    get("/dokument") {
        withSubject(AuthLevel.Level4) { subject ->
            withAudit(describe(subject, READ, Journalpost)) {
                call.respond(
                    safService.hentJournalposter(subject)
                )
            }
        }
    }

    get("/dokument/{journalpostId}") {
        withSubject(AuthLevel.Level4) { subject ->
            withAudit(describe(subject, READ, Journalpost, JOURNALPOST to call.parameters["journalpostId"])) {
                val journalpostId = requireNotNull(call.parameters["journalpostId"]) {
                    "journalpostId må være spesifisert"
                }
                val journalpost = safService.hentJournalpost(subject, journalpostId)
                if (journalpost == null) {
                    call.respond(HttpStatusCode.NotFound)
                } else {
                    call.respond(journalpost)
                }
            }
        }
    }

    get("/dokument/{journalpostId}/{dokumentId}") {
        withSubject(AuthLevel.Level4) { subject ->
            withAudit(describe(subject, READ, Dokument, JOURNALPOST to call.parameters["journalpostId"], DOKUMENT to call.parameters["dokumentId"])) {
                val journalpostId = requireNotNull(call.parameters["journalpostId"]) {
                    "journalpostId må være spesifisert"
                }
                val dokumentId = requireNotNull(call.parameters["dokumentId"]) {
                    "dokumentId må være spesifisert"
                }
                call.respondBytes(
                    contentType = ContentType.Application.Pdf,
                    status = HttpStatusCode.OK,
                    bytes = safService.hentDokument(subject, journalpostId, dokumentId)
                )
            }
        }
    }
}
