package no.nav.sbl.dialogarena.mininnboks.consumer.domain

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

class Traad(meldinger: List<Henvendelse?>?) {
    var traadId: String? = null
    var meldinger: List<Henvendelse?>? = null
    var nyeste: Henvendelse? = null
    var eldste: Henvendelse? = null
    var kanBesvares: Boolean = false
    var avsluttet: Boolean? = null

    companion object {
        private val FRA_NAV = Arrays.asList(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE, Henvendelsetype.INFOMELDING_MODIA_UTGAAENDE, Henvendelsetype.SVAR_SKRIFTLIG, Henvendelsetype.SVAR_OPPMOTE, Henvendelsetype.SVAR_TELEFON, Henvendelsetype.SAMTALEREFERAT_OPPMOTE, Henvendelsetype.SAMTALEREFERAT_TELEFON, Henvendelsetype.DOKUMENT_VARSEL)
        private val KAN_BESVARES = Arrays.asList(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE)
        val NYESTE_OPPRETTET = Function { traad: Traad -> traad.nyeste?.opprettet }
        val NYESTE_FORST = Collections.reverseOrder(Comparator.comparing(NYESTE_OPPRETTET))
    }

    init {
        if (meldinger != null) {
            this.meldinger = meldinger
                    .sortedWith(Henvendelse.NYESTE_OVERST)

            nyeste = this.meldinger!![0]!!
            eldste = this.meldinger!![this.meldinger!!.size - 1]!!
            kanBesvares = !nyeste?.kassert!! && KAN_BESVARES.contains(nyeste?.type)
            val avsluttet = FRA_NAV.contains(nyeste?.type) && !kanBesvares
            val ferdigstiltUtenSvar = java.lang.Boolean.TRUE == nyeste?.ferdigstiltUtenSvar
            this.avsluttet = avsluttet || ferdigstiltUtenSvar
            traadId = nyeste?.traadId
        }
    }
}
