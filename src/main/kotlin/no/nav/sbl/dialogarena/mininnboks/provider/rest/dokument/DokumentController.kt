package no.nav.sbl.dialogarena.mininnboks.provider.rest.dokument

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import no.nav.sbl.dialogarena.mininnboks.AuthLevel
import no.nav.sbl.dialogarena.mininnboks.consumer.saf.SafService
import no.nav.sbl.dialogarena.mininnboks.requireNotEmpty
import no.nav.sbl.dialogarena.mininnboks.withSubject

fun Route.dokumentController(safService: SafService) {
    get("/journalposter") {
        withSubject(AuthLevel.Level4) { subject ->
            call.respond(
                safService.hentJournalposter(subject)
            )
        }
    }

    get("/dokument/{journalpostId}") {
        withSubject(AuthLevel.Level4) { subject ->
            val journalpostId = requireNotNull(call.parameters["journalpostId"]) {
                "journalpostId må være spesifisert"
            }
            val dokumentIdListe = requireNotEmpty(call.request.queryParameters.getAll("dokumentId")) {
                "Minst ett dokument må være spesifisert"
            }

            val journalpost = safService.hentJournalpost(subject, journalpostId, dokumentIdListe)
            if (journalpost == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(journalpost)
            }
        }
    }

    get("/dokument/{journalpostId}/{dokumentId}") {
        withSubject(AuthLevel.Level4) { subject ->
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
