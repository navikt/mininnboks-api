package no.nav.sbl.dialogarena.mininnboks.consumer.domain;

import java.io.Serializable;

public enum Henvendelsetype implements Serializable {
    SPORSMAL_SKRIFTLIG,
    SVAR_SKRIFTLIG,
    SVAR_OPPMOTE,
    SVAR_TELEFON,
    SAMTALEREFERAT_OPPMOTE,
    SVAR_SBL_INNGAAENDE,
    SPORSMAL_MODIA_UTGAAENDE,
    SAMTALEREFERAT_TELEFON,
    DOKUMENT_VARSEL,
    OPPGAVE_VARSEL
}
