package no.nav.sbl.dialogarena.mininnboks.consumer.domain

import no.nav.common.utils.EnvironmentUtils.getRequiredProperty

enum class Temagruppe {
    ARBD, HELSE, FMLI, FDAG, HJLPM, BIL, ORT_HJE, OVRG, PENS, UFRT, OKSOS, ANSOS;

    companion object {
        val GODKJENTE_TEMAGRUPPER_PROPERTY = "GODKJENTE_TEMAGRUPPER"
        val GODKJENTE_FOR_INNGAAENDE_SPORSMAAL: List<Temagruppe> by lazy {
            getRequiredProperty(GODKJENTE_TEMAGRUPPER_PROPERTY)
                .split(",")
                .map(Temagruppe::valueOf)
        }
    }
}
