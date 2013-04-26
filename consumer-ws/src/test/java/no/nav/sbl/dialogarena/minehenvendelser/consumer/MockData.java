package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HentBrukerBehandlingerResponse;

import java.util.HashMap;
import java.util.Map;

public class MockData {

    private Map<String, HentBrukerBehandlingerResponse> responses = new HashMap<>();

    public MockData() {
    }

    public HentBrukerBehandlingerResponse getData(String aktorId) {
        if (responses.containsKey(aktorId)) {
            return responses.get(aktorId);
        }
        return emptyResponse();
    }

    private HentBrukerBehandlingerResponse emptyResponse() {
        return new HentBrukerBehandlingerResponse();
    }

    public void addResponse(String aktorId, HentBrukerBehandlingerResponse response) {
        responses.put(aktorId, response);
    }

    public void clear() {
        responses.clear();
    }
}
