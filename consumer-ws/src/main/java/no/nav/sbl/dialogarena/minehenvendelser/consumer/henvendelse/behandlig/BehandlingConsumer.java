package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandlig;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandlig.BehandlingDTO.Behandlingsstatus.FERDIG;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandlig.BehandlingDTO.Behandlingsstatus.UNDER_ARBEID;

public class BehandlingConsumer {

    public List<BehandlingDTO> hentBehandlinger(String aktorId) {
        List<BehandlingDTO> behandlinger = new ArrayList<>();
        
        BehandlingDTO behandling = BehandlingDTO.getBuilder()
        		.brukerBehandlingsId("ID")
        		.status(UNDER_ARBEID)
        		.create();
        behandlinger.add(behandling);
        behandling = BehandlingDTO.getBuilder()
        		.brukerBehandlingsId("ID2")
        		.status(FERDIG).create();
        behandlinger.add(behandling);

        behandling = BehandlingDTO.getBuilder()
        		.brukerBehandlingsId("ID3")
        		.status(FERDIG).create();
        behandlinger.add(behandling);

        return behandlinger;

    }
}
