package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandlinger;

public interface BehandlingService {

    Behandlinger hentBehandlinger(String aktoerId);

}
