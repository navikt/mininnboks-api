package no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;

public class FitInnsendtBehandling {

    public String behandlingsId;
    public String brukerBehandlingstype;
    public String dokumentBehandlingstype;
    public String antallVedlegg;
    public String navnPaaBehandling;
    public String dato;
    public String innsendteDokumenter;
    public String manglendeDokumenter;

    public FitInnsendtBehandling() {}

    public FitInnsendtBehandling(Behandling behandling) {
        behandlingsId = behandling.getBehandlingsId();
        antallVedlegg = Integer.valueOf(behandling.fetchAlleDokumenter().size()).toString();
        navnPaaBehandling = behandling.getTittel();
        dato = behandling.getInnsendtDato().toString();


        //TODO
    }

    public FitInnsendtBehandling(String antallVedlegg, String innsendtDato, String tittel, String innsendte, String manglende) {
        this.antallVedlegg = antallVedlegg;
        this.dato = innsendtDato;
        this.navnPaaBehandling =  tittel;
        this.innsendteDokumenter = innsendte;
        this.manglendeDokumenter = manglende;
    }
}
