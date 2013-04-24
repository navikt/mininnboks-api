package no.nav.sbl.dialogarena.minehenvendelser.wsmock.consumer;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;

import java.util.ArrayList;
import java.util.List;

public class BehandlingServiceFilebased implements BehandlingService {


    @Override
    public List<Behandling> hentBehandlinger(String aktoerId) {
        List<Behandling> behandlinger = new ArrayList<>();
        return behandlinger;
    }
}
