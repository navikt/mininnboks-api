package no.nav.sbl.dialogarena.mininnboks.provider

import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype

object LinkService {

    const val MININNBOKS_LINK_PROPERTY = "MININNBOKS_LINK_URL"
    private val MININNBOKS_LINK = EnvironmentUtils.getRequiredProperty(MININNBOKS_LINK_PROPERTY)
    const val TEMAVELGER_LINK_PROPERTY = "TEMAVELGER_LINK_URL"
    val TEMAVELGER_LINK: String = EnvironmentUtils.getRequiredProperty(TEMAVELGER_LINK_PROPERTY)
    const val BRUKERPROFIL_LINK_PROPERTY = "BRUKERPROFIL_LINK_URL"
    val BRUKERPROFIL_LINK: String = EnvironmentUtils.getRequiredProperty(BRUKERPROFIL_LINK_PROPERTY)
    const val SAKSOVERSIKT_LINK_PROPERTY = "SAKSOVERSIKT_LINK_URL"
    val SAKSOVERSIKT_LINK: String = EnvironmentUtils.getRequiredProperty(SAKSOVERSIKT_LINK_PROPERTY)

    fun lagDirektelenkeTilMelding(henvendelse: Henvendelse): String {
        return if (Henvendelsetype.DOKUMENT_VARSEL == henvendelse.type || Henvendelsetype.OPPGAVE_VARSEL == henvendelse.type) {
            String.format("%s/?varselid=%s", MININNBOKS_LINK, henvendelse.korrelasjonsId)
        } else String.format("%s/traad/%s", MININNBOKS_LINK, henvendelse.traadId)
    }
}
