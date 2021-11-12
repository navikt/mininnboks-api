package no.nav.sbl.dialogarena.mininnboks.consumer.saf

import no.nav.sbl.dialogarena.mininnboks.consumer.saf.queries.HentDokumentdata
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is
import org.spekframework.spek2.Spek
import java.time.LocalDateTime

object SafServiceImplTest : Spek({
    val journalpost = HentDokumentdata.Journalpost(
        journalpostId = "123456",
        tittel = "Tittel",
        journalposttype = HentDokumentdata.Journalposttype.U,
        tema = "AAP",
        avsender = null,
        mottaker = null,
        relevanteDatoer = listOf(
            HentDokumentdata.RelevantDato(
                dato = LocalDateTime.parse("2021-01-01T12:00:00"),
                datotype = HentDokumentdata.Datotype.DATO_OPPRETTET
            ),
            HentDokumentdata.RelevantDato(
                dato = LocalDateTime.parse("2021-01-02T12:00:00"),
                datotype = HentDokumentdata.Datotype.DATO_REGISTRERT
            ),
            HentDokumentdata.RelevantDato(
                dato = LocalDateTime.parse("2021-01-03T12:00:00"),
                datotype = HentDokumentdata.Datotype.DATO_DOKUMENT
            ),
            HentDokumentdata.RelevantDato(
                dato = LocalDateTime.parse("2021-01-04T12:00:00"),
                datotype = HentDokumentdata.Datotype.DATO_JOURNALFOERT
            ),
            HentDokumentdata.RelevantDato(
                dato = LocalDateTime.parse("2021-01-05T12:00:00"),
                datotype = HentDokumentdata.Datotype.DATO_SENDT_PRINT
            ),
            HentDokumentdata.RelevantDato(
                dato = LocalDateTime.parse("2021-01-06T12:00:00"),
                datotype = HentDokumentdata.Datotype.DATO_EKSPEDERT
            )
        ),
        dokumenter = emptyList()
    )

    test("finne relevant dato fra inngående journalpost") {
        val inngaende = journalpost.copy(journalposttype = HentDokumentdata.Journalposttype.I)
        assertThat(SafServiceImpl.getDato(inngaende), Is.`is`(LocalDateTime.parse("2021-01-02T12:00:00")))
    }

    test("finne relevant dato fra utgående journalpost") {
        val utgaende = journalpost.copy(journalposttype = HentDokumentdata.Journalposttype.U)
        assertThat(SafServiceImpl.getDato(utgaende), Is.`is`(LocalDateTime.parse("2021-01-06T12:00:00")))
    }

    test("finne relevant dato fra notat journalpost") {
        val notat = journalpost.copy(journalposttype = HentDokumentdata.Journalposttype.N)
        assertThat(SafServiceImpl.getDato(notat), Is.`is`(LocalDateTime.parse("2021-01-04T12:00:00")))
    }
})
