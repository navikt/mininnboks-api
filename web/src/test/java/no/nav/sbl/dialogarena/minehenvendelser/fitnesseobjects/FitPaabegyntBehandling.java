package no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;

public class FitPaabegyntBehandling {

    public String behandlingsId;

    public FitPaabegyntBehandling() {}

    public FitPaabegyntBehandling(Behandling behandling) {
        behandlingsId = behandling.getBehandlingsId();
    }

}
