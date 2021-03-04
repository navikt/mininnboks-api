package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import java.util.*
import kotlin.collections.ArrayList

enum class Status {
    ULEST, UBESVART
}

class UbehandletMelding(
    val behandlingskjedeId: String?,
    val opprettetDato: Date?,
    val type: Henvendelsetype?,
    val undertype: String?,
    val uri: String?,
    val statuser: MutableList<Status> = ArrayList(),
    val varselid: String?
) {

    companion object {
        val erIkkeKassert: (Henvendelse?) -> Boolean = { henvendelse: Henvendelse? -> !henvendelse?.kassert!! }
        var erUbesvart: (Henvendelse?) -> Boolean = { henvendelse: Henvendelse? -> henvendelse?.type == Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE }
        var erUlest: (Henvendelse?) -> Boolean = { henvendelse: Henvendelse? -> !henvendelse?.isLest!! }
    }
}
