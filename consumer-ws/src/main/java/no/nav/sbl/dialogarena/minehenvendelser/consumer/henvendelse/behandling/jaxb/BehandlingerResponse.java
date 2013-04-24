package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import java.util.ArrayList;
import java.util.List;

public class BehandlingerResponse {

    private List<Behandling> behandlinger = new ArrayList<>();

    public List<Behandling> getBehandlinger() {
        return behandlinger;
    }
}
