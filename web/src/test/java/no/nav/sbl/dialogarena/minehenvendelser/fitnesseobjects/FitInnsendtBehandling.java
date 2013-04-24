package no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.Behandling;

public class FitInnsendtBehandling {

    public String behandlingsId;
    public String brukerbehandlingstype;
    public String dokumentbehandlingstype;
    public String antallvedlegg;
    public String navnPaaBehandling;
    public String dato;
    public String innsendteDokumenter;
    public String manglendeDokumenter;

    public FitInnsendtBehandling() {}

    public FitInnsendtBehandling(Behandling behandling) {
        behandlingsId = behandling.getBehandlingsId();
        //TODO
    }

}
