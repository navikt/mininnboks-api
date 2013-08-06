package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.FinnSakOgBehandlingskjedeListeResponse;

import java.util.HashMap;
import java.util.Map;

public class MockData {

    private Map<String, FinnSakOgBehandlingskjedeListeResponse> responses = new HashMap<>();

    public FinnSakOgBehandlingskjedeListeResponse getData(String aktorId) {
        if (responses.containsKey(aktorId)) {
            return responses.get(aktorId);
        }
        return emptyResponse();
    }

    private FinnSakOgBehandlingskjedeListeResponse emptyResponse() {
        return new FinnSakOgBehandlingskjedeListeResponse();
    }

    public void addResponse(String aktorId, FinnSakOgBehandlingskjedeListeResponse response) {
        responses.put(aktorId, response);
    }

    public void clear() {
        responses.clear();
    }
}
