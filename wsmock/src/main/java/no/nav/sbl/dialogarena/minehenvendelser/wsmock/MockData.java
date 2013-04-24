package no.nav.sbl.dialogarena.minehenvendelser.wsmock;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingerResponse;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingerResponse;

import java.util.List;

public class MockData {

    private BehandlingerResponse behandlingerResponse;
    private boolean responseCleared = false;

    public MockData() {
        //TODO instantiate behandlingerResponse with static data
    }

    public void clearResponse() {
        if (!responseCleared) {
            this.behandlingerResponse = new BehandlingerResponse();
            responseCleared = true;
        }
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
