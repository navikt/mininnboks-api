package no.nav.sbl.dialogarena.minehenvendelser.wsmock;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;

import java.util.ArrayList;
import java.util.List;

public class MockData {

    private List<Behandling> behandlinger;

    public MockData() {
        //TODO instantiate behandlingerResponse with static data
    }

    public void updateMockData(List<Behandling> behandlinger) {
        this.behandlinger = behandlinger;
    }

    public void addBehandlingToMockData(Behandling behandling) {
        if (behandlinger == null) {
            behandlinger = new ArrayList<>();
        }
        behandlinger.add(behandling);
    }

    public void clearResponse() {
        behandlinger = new ArrayList<>();
    }
}
