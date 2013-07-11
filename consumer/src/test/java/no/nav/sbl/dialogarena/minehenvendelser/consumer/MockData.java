package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;

import java.util.HashMap;
import java.util.Map;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.meldinger.HentBrukerBehandlingListeResponse;

public class MockData {

    private Map<String, HentBrukerBehandlingListeResponse> responses = new HashMap<>();

    public MockData() {
    }

    public HentBrukerBehandlingListeResponse getData(String aktorId) {
        if (responses.containsKey(aktorId)) {
            return responses.get(aktorId);
        }
        return emptyResponse();
    }

    private HentBrukerBehandlingListeResponse emptyResponse() {
        return new HentBrukerBehandlingListeResponse();
    }

    public void addBehandlingToAktor(String aktorId, WSBrukerBehandlingOppsummering behandling) {
        HentBrukerBehandlingListeResponse response;
        if (responses.containsKey(aktorId)) {
            response = responses.get(aktorId);
        } else {
            response = new HentBrukerBehandlingListeResponse();
        }
        response.getBrukerBehandlinger().add(behandling);
        responses.put(aktorId, response);
    }

    public void addResponse(String aktorId, HentBrukerBehandlingListeResponse response) {
        responses.put(aktorId, response);
    }

    public void clear() {
        responses.clear();
    }
}
