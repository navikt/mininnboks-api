package no.nav.sbl.dialogarena.mininnboks.consumer.domain

import java.util.*

enum class Temagruppe {
    ARBD, FMLI, HJLPM, BIL, ORT_HJE, OVRG, PENS, UFRT, OKSOS, ANSOS;

    companion object {
        val GODKJENTE_FOR_INNGAAENDE_SPORSMAAL = Arrays.asList(ARBD, FMLI, HJLPM, BIL, ORT_HJE, PENS, UFRT)
    }
}
