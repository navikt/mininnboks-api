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

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createBehandlingResponse;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createBehandlingskjedensBehandlingerResponse;

public class SakOgBehandlingPortTypeMock implements SakOgBehandlingPortType {

    @Inject
    MockData mockData;

    @Override
    public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(FinnSakOgBehandlingskjedeListeRequest request) {
        return mockData.getFinnData().getData(request.getAktoerREF()).getResponse();
    }

    @Override
    public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(HentBehandlingskjedensBehandlingerRequest request) {
        return createBehandlingskjedensBehandlingerResponse();
    }

    @Override
    public HentBehandlingResponse hentBehandling (HentBehandlingRequest request) {
        return createBehandlingResponse();
    }

    @Override
    public void ping() {
    }
}
