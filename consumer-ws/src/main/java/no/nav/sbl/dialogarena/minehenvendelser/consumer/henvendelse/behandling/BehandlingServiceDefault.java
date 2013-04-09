package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;

import java.util.List;

public class BehandlingServiceDefault implements BehandlingService {
    @Override
    public List<Behandling> hentBehandlinger(String aktoerId) {
        throw new UnsupportedOperationException("BehandlingService is not yet implemented");
    }
}
