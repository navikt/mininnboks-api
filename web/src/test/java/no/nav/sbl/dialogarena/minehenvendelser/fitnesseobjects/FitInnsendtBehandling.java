package no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects;

public class FitInnsendtBehandling {

    public String behandlingsId;
    public String brukerBehandlingstype;
    public String dokumentBehandlingstype;
    public String antallSendteAvTotaltAntallDokumenter;
    public String navnPaaBehandling;
    public String datoOgTidspunkt;
    public String innsendteDokumenter;
    public String manglendeDokumenter;

    public FitInnsendtBehandling() {
    }

    public FitInnsendtBehandling(String antallVedlegg, String innsendtDato, String tittel, String innsendte, String manglende) {
        this.antallSendteAvTotaltAntallDokumenter = antallVedlegg;
        this.datoOgTidspunkt = innsendtDato;
        this.navnPaaBehandling = tittel;
        this.innsendteDokumenter = innsendte;
        this.manglendeDokumenter = manglende;
    }
}
