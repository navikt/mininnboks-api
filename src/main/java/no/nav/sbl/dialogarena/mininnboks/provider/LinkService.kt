package no.nav.sbl.dialogarena.mininnboks.provider

import lombok.extern.slf4j.Slf4j
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import no.nav.sbl.util.EnvironmentUtils

@Slf4j
object LinkService {

    const val MININNBOKS_LINK_PROPERTY = "MININNBOKS_LINK_URL"
    private val MININNBOKS_LINK = EnvironmentUtils.getRequiredProperty(MININNBOKS_LINK_PROPERTY)
    const val TEMAVELGER_LINK_PROPERTY = "TEMAVELGER_LINK_URL"
    @JvmField
    val TEMAVELGER_LINK = EnvironmentUtils.getRequiredProperty(TEMAVELGER_LINK_PROPERTY)
    const val BRUKERPROFIL_LINK_PROPERTY = "BRUKERPROFIL_LINK_URL"
    @JvmField
    val BRUKERPROFIL_LINK = EnvironmentUtils.getRequiredProperty(BRUKERPROFIL_LINK_PROPERTY)
    const val SAKSOVERSIKT_LINK_PROPERTY = "SAKSOVERSIKT_LINK_URL"
    @JvmField
    val SAKSOVERSIKT_LINK = EnvironmentUtils.getRequiredProperty(SAKSOVERSIKT_LINK_PROPERTY)
    @JvmStatic
    fun lagDirektelenkeTilMelding(henvendelse: Henvendelse): String {
        return if (Henvendelsetype.DOKUMENT_VARSEL == henvendelse.type || Henvendelsetype.OPPGAVE_VARSEL == henvendelse.type) {
            String.format("%s/?varselid=%s", MININNBOKS_LINK, henvendelse.korrelasjonsId)
        } else String.format("%s/traad/%s", MININNBOKS_LINK, henvendelse.traadId)
    }

    @JvmStatic
    fun touch() {
     /*   log.info(MININNBOKS_LINK)
        log.info(TEMAVELGER_LINK)
        log.info(BRUKERPROFIL_LINK)
        log.info(SAKSOVERSIKT_LINK)*/
    }
}
