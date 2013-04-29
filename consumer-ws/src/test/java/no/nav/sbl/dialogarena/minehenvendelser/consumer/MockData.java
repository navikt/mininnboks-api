package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
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

    public void addBehandlingToAktor(String aktorId, WSBrukerBehandling behandling) {
        HentBrukerBehandlingerResponse response;
        if (responses.containsKey(aktorId)) {
            response = responses.get(aktorId);
        } else {
            response = new HentBrukerBehandlingerResponse();
        }
        response.getBrukerBehandlinger().add(behandling);
        responses.put(aktorId,response);
    }

    public void addResponse(String aktorId, HentBrukerBehandlingerResponse response) {
        System.out.println(response.toString());
        responses.put(aktorId, response);
    }

    public void clear() {
        responses.clear();
    }
}
