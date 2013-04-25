package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HentBrukerBehandlingerResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createWsBehandlingMock;

public class MockData {

    private Map<String, HentBrukerBehandlingerResponse> responses = new HashMap<>();

    public MockData() {
        HentBrukerBehandlingerResponse response = new HentBrukerBehandlingerResponse();
        WSBrukerBehandling behandling = createWsBehandlingMock();
        response.withBrukerBehandlinger(behandling);
        addResponse("test",response);
    }

    public HentBrukerBehandlingerResponse getData(String aktorId){
        if(responses.containsKey(aktorId)){
            return responses.get(aktorId);
        }
        return null;
    }

    public void addResponse(String aktorId ,HentBrukerBehandlingerResponse response) {
        responses.put(aktorId, response);
    }

    public void clear() {
        responses.clear();
    }
}
