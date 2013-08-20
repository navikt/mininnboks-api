package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.meldinger.HentBrukerBehandlingListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingskjedensBehandlingerResponse;

import java.util.HashMap;
import java.util.Map;

public class MockData {

    private MockFinnData mockFinnData;
    private MockHentData mockHentData;
    private MockHentBehandlingskjedensBehandlingerData mockHentBehandlingskjedensBehandlingerData;

    public MockData() {
        mockFinnData = new MockFinnData();
        mockHentData = new MockHentData();
        mockHentBehandlingskjedensBehandlingerData = new MockHentBehandlingskjedensBehandlingerData();
    }

    public MockFinnData getFinnData() {
        return mockFinnData;
    }

    public MockHentData getHentData() {
        return mockHentData;
    }

    public MockHentBehandlingskjedensBehandlingerData getMockHentBehandlingskjedensBehandlingerData() {
        return mockHentBehandlingskjedensBehandlingerData;
    }

    public void clear() {
        mockFinnData.clear();
        mockHentData.clear();
    }

    public class MockHentBehandlingskjedensBehandlingerData {
        private Map<String, HentBehandlingskjedensBehandlingerResponse> responses = new HashMap<>();

        public HentBehandlingskjedensBehandlingerResponse getData(String behandlingskjedeId) {
            if (responses.containsKey(behandlingskjedeId)) {
                return responses.get(behandlingskjedeId);
            }
            return emptyResponse();
        }

        private HentBehandlingskjedensBehandlingerResponse emptyResponse() {
            return new HentBehandlingskjedensBehandlingerResponse();
        }

        public void addResponse(String behandlingskjedeId, HentBehandlingskjedensBehandlingerResponse response) {
            responses.put(behandlingskjedeId, response);
        }

        public void clear() {
            responses.clear();
        }

    }

    public class MockHentData {
        private Map<String, HentBrukerBehandlingListeResponse> responses = new HashMap<>();

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

    public class MockFinnData {
        private Map<String, FinnSakOgBehandlingskjedeListeResponse> responses = new HashMap<>();

        public FinnSakOgBehandlingskjedeListeResponse getData(String aktorId) {
            if (responses.containsKey(aktorId)) {
                return responses.get(aktorId);
            }
            return emptyFinnResponse();
        }

        private FinnSakOgBehandlingskjedeListeResponse emptyFinnResponse() {
            return new FinnSakOgBehandlingskjedeListeResponse();
        }

        public void addResponse(String aktorId, FinnSakOgBehandlingskjedeListeResponse response) {
            responses.put(aktorId, response);
        }

        public void clear() {
            responses.clear();
        }
    }
}
