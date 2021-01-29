package no.nav.sbl.dialogarena.mininnboks.consumer.domain

enum class Temagruppe {
    ARBD, HELSE, FMLI, FDAG, HJLPM, BIL, ORT_HJE, OVRG, PENS, UFRT, OKSOS, ANSOS;

    companion object {
        val GODKJENTE_FOR_INNGAAENDE_SPORSMAAL = listOf(ARBD, HELSE, FMLI, FDAG, HJLPM, BIL, ORT_HJE, PENS, UFRT, OVRG)
    }
}
