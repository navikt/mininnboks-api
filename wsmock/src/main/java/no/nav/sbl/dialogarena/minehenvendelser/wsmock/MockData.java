package no.nav.sbl.dialogarena.minehenvendelser.wsmock;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.BehandlingerResponse;

import java.util.List;

public class MockData {

    private BehandlingerResponse behandlingerResponse;

    public MockData() {
        //TODO instantiate behandlingerResponse with static data
    }

    public void clearResponse() {
        this.behandlingerResponse = new BehandlingerResponse();
    }

    public void addBehandlingerToResponse(List<Behandling> behandlinger) {
        for (Behandling behandling : behandlinger) {
            behandlingerResponse.getBehandlinger().add(behandling);
        }
    }

    public BehandlingerResponse getBehandlingerResponse() {
        return behandlingerResponse;
    }
}
