package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import no.nav.sbl.dialogarena.mininnboks.provider.LinkService.lagDirektelenkeTilMelding
import java.util.*

class UbehandletMelding(henvendelse: Henvendelse) {
    enum class Status {
        ULEST, UBESVART
    }

    var behandlingskjedeId: String? = null
    var opprettetDato: Date? = null
    var type: Henvendelsetype? = null
    var undertype: String? = null
    var uri: String? = null
    var statuser: MutableList<Status> = ArrayList()
    var varselid: String? = null

    companion object {
        val erIkkeKassert: (Henvendelse?) -> Boolean = { henvendelse: Henvendelse? -> !henvendelse?.kassert!! }
        var erUbesvart: (Henvendelse?) -> Boolean = { henvendelse: Henvendelse? -> henvendelse?.type == Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE }
        var erUlest: (Henvendelse?) -> Boolean = { henvendelse: Henvendelse? -> !henvendelse?.isLest!! }
    }

    init {
        behandlingskjedeId = henvendelse.traadId
        opprettetDato = henvendelse.opprettet
        type = henvendelse.type
        undertype = henvendelse.oppgaveType
        uri = lagDirektelenkeTilMelding(henvendelse)
        varselid = henvendelse.korrelasjonsId
        if (erUbesvart.invoke(henvendelse)) {
            statuser.add(Status.UBESVART)
        }
        if (erUlest.invoke(henvendelse)) {
            statuser.add(Status.ULEST)
        }
    }
}
