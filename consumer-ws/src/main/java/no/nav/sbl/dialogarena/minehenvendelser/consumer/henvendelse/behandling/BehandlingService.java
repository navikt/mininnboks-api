package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import java.util.List;

public interface BehandlingService {

    List<Behandling> hentBehandlinger(String aktoerId);

}
