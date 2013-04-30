package no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;

public class FitPaabegyntBehandling {

    public String navnPaaBehandling;
    public String dato;
    public String antallDokumenterLastetOpp;

    public FitPaabegyntBehandling() {
    }

    public FitPaabegyntBehandling(String tittel, String antall, String sistEndret) {
        this.antallDokumenterLastetOpp = antall;
        this.navnPaaBehandling = tittel;
        this.dato = sistEndret;

    }
}
