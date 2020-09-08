package no.nav.sbl.dialogarena.mininnboks.consumer.domain

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class Henvendelse : Serializable {
    var id: String? = null
    var traadId: String? = null
    var fritekst: String? = null
    var kanal: String? = null
    var eksternAktor: String? = null
    var brukersEnhet: String? = null
    var tilknyttetEnhet: String? = null
    var temagruppeNavn: String? = null
    var statusTekst: String? = null
    var kontorsperreEnhet: String? = null
    var temaNavn: String? = null
    var temaKode: String? = null
    var korrelasjonsId: String? = null
    var journalpostId: String? = null
    var dokumentIdListe: ArrayList<String> = ArrayList()
    var oppgaveType: String? = null
    var oppgaveUrl: String? = null
    var type: Henvendelsetype? = null
    var temagruppe: Temagruppe? = null
    var opprettet: Date? = null
    var avsluttet: Date? = null
    var fraNav: Boolean? = null
    var fraBruker: Boolean? = null
    var kassert = false
    var erTilknyttetAnsatt: Boolean? = null
    var ferdigstiltUtenSvar: Boolean? = null
    var lestDato: Date? = null

    constructor(id: String?) {
        this.id = id
    }

    constructor(fritekst: String?, temagruppe: Temagruppe?) {
        this.fritekst = fritekst
        this.temagruppe = temagruppe
    }

    fun withTraadId(traadId: String?): Henvendelse {
        this.traadId = traadId
        return this
    }

    fun withType(type: Henvendelsetype?): Henvendelse {
        this.type = type
        return this
    }

    fun withOpprettetTid(opprettetTid: Date?): Henvendelse {
        opprettet = opprettetTid
        return this
    }

    fun markerSomLest(lestDato: Date?) {
        this.lestDato = lestDato
    }

    fun markerSomLest() {
        lestDato = Date()
    }

    val isLest: Boolean
        get() = lestDato != null

    fun withTemaNavn(temaNavn: String?): Henvendelse {
        this.temaNavn = temaNavn
        return this
    }

    companion object {
        val NYESTE_OVERST = Collections.reverseOrder(Comparator.comparing { henvendelse: Henvendelse -> henvendelse.opprettet })
    }
}
