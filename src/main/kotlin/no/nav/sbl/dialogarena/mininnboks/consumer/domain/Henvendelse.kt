package no.nav.sbl.dialogarena.mininnboks.consumer.domain

import java.util.*
import kotlin.collections.ArrayList

data class Henvendelse(val id: String? = null,
                       val traadId: String? = null,
                       val fritekst: String? = null,
                       val kanal: String? = null,
                       val eksternAktor: String? = null,
                       val brukersEnhet: String? = null,
                       val tilknyttetEnhet: String? = null,
                       val temagruppeNavn: String? = null,
                       val statusTekst: String? = null,
                       val kontorsperreEnhet: String? = null,
                       val temaNavn: String? = null,
                       val temaKode: String? = null,
                       val korrelasjonsId: String? = null,
                       val journalpostId: String? = null,
                       val dokumentIdListe: ArrayList<String> = ArrayList(),
                       val oppgaveType: String? = null,
                       val oppgaveUrl: String? = null,
                       val type: Henvendelsetype,
                       val temagruppe: Temagruppe? = null,
                       val opprettet: Date? = null,
                       val avsluttet: Date? = null,
                       val fraNav: Boolean? = null,
                       val fraBruker: Boolean? = null,
                       val kassert: Boolean = false,
                       val erTilknyttetAnsatt: Boolean? = null,
                       val ferdigstiltUtenSvar: Boolean? = null,
                       val lestDato: Date? = null) {

    val isLest: Boolean
        get() = lestDato != null

    companion object {
        val NYESTE_OVERST: Comparator<Henvendelse?> = Collections.reverseOrder(Comparator.comparing { henvendelse: Henvendelse -> henvendelse.opprettet })
    }
}
