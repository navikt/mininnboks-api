package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.Behandling;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;

import javax.inject.Inject;
import java.util.List;

public class InformasjonService implements BehandlingService {

    @Inject
    private HenvendelsesBehandlingPortType  service;

    public List<Behandling> hentBehandlinger(String aktoerId){
        return null;
    }

}
