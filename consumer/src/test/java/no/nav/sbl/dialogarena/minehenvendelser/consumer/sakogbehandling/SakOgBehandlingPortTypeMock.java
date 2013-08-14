package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;

import javax.inject.Inject;

public class SakOgBehandlingPortTypeMock implements SakOgBehandlingPortType {

    @Inject
    private MockData mockData;

    @Override
    public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(FinnSakOgBehandlingskjedeListeRequest request) {
        return mockData.getFinnData().getData(request.getAktoerREF()).getResponse();
    }

    @Override
    public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(HentBehandlingskjedensBehandlingerRequest request) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public HentBehandlingResponse hentBehandling (HentBehandlingRequest request) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public void ping() {
    }
}
