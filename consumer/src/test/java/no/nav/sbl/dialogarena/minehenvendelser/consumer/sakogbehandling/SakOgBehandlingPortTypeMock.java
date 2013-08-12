package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandling;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createHentBehandlingResponse;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createHentBehandlingskjedensBehandlingerResponse;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createBehandling;

public class SakOgBehandlingPortTypeMock implements SakOgBehandlingPortType {

    @Inject
    MockData mockData;

    @Override
    public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(FinnSakOgBehandlingskjedeListeRequest request) {
        return mockData.getFinnData().getData(request.getAktoerREF()).getResponse();
    }

    @Override
    public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(HentBehandlingskjedensBehandlingerRequest request) {
        return createHentBehandlingskjedensBehandlingerResponse(populateBehandlingerList());
    }

    @Override
    public HentBehandlingResponse hentBehandling (HentBehandlingRequest request) {
        return createHentBehandlingResponse(BigInteger.valueOf(99));
    }

    @Override
    public void ping() {
    }

    private static List<Behandling> populateBehandlingerList() {
        List<Behandling> behandlinger = new ArrayList<>();
        behandlinger.add(createBehandling(BigInteger.valueOf(1)));
        behandlinger.add(createBehandling(BigInteger.valueOf(2)));
        behandlinger.add(createBehandling(BigInteger.valueOf(3)));
        behandlinger.add(createBehandling(BigInteger.valueOf(4)));
        return behandlinger;
    }

}
