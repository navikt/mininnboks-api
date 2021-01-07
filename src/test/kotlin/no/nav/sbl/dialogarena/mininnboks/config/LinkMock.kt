package no.nav.sbl.dialogarena.mininnboks.config

import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.mininnboks.provider.LinkService

object LinkMock {
    @JvmStatic
    fun setup() {
        EnvironmentUtils.setProperty(LinkService.MININNBOKS_LINK_PROPERTY, "/", EnvironmentUtils.Type.PUBLIC)
        EnvironmentUtils.setProperty(LinkService.SAKSOVERSIKT_LINK_PROPERTY, "/", EnvironmentUtils.Type.PUBLIC)
        EnvironmentUtils.setProperty(LinkService.TEMAVELGER_LINK_PROPERTY, "/", EnvironmentUtils.Type.PUBLIC)
        EnvironmentUtils.setProperty(LinkService.BRUKERPROFIL_LINK_PROPERTY, "/", EnvironmentUtils.Type.PUBLIC)
    }
}
