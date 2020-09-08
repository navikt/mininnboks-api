package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import no.nav.sbl.dialogarena.mininnboks.provider.LinkService.lagDirektelenkeTilMelding
import java.util.*
import java.util.function.Predicate

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
        @JvmField
        var erIkkeKassert = Predicate { henvendelse: Henvendelse -> !henvendelse.kassert }
        @JvmField
        var erUbesvart = Predicate { henvendelse: Henvendelse -> henvendelse.type == Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE }
        @JvmField
        var erUlest = Predicate { henvendelse: Henvendelse -> !henvendelse.isLest }
    }

    init {
        behandlingskjedeId = henvendelse.traadId
        opprettetDato = henvendelse.opprettet
        type = henvendelse.type
        undertype = henvendelse?.oppgaveType
        uri = lagDirektelenkeTilMelding(henvendelse)
        varselid = henvendelse?.korrelasjonsId
        if (erUbesvart.test(henvendelse)) {
            statuser.add(Status.UBESVART)
        }
        if (erUlest.test(henvendelse)) {
            statuser.add(Status.ULEST)
        }
    }
}
