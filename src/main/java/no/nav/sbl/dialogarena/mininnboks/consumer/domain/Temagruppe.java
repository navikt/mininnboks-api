package no.nav.sbl.dialogarena.mininnboks.consumer.domain;

import java.util.List;

import static java.util.Arrays.asList;

public enum Temagruppe {
    ARBD,
    FMLI,
    HJLPM,
    BIL,
    ORT_HJE,
    OVRG,
    PENS,
    UFRT,
    OKSOS,
    ANSOS;

    public static final List<Temagruppe> GODKJENTE_FOR_INNGAAENDE_SPORSMAAL = asList(ARBD, FMLI, HJLPM, BIL, ORT_HJE, OKSOS, PENS, UFRT);
}
