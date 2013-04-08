package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;

import javax.inject.Inject;
import java.util.List;

public class BehandlingConsumer {

    @Inject
    private BehandlingService behandlingService;

    public List<Behandling> hentBehandlinger(String aktoerId) {
        return behandlingService.hentBehandlinger(aktoerId).getBehandlingerList();
    }
}
