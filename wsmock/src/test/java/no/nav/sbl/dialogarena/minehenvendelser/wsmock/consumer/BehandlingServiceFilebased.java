package no.nav.sbl.dialogarena.minehenvendelser.wsmock.consumer;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.BehandlingerResponse;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.inject.Inject;
import javax.xml.transform.stream.StreamSource;
import java.util.List;

public class BehandlingServiceFilebased implements BehandlingService {

    @Inject
    private Jaxb2Marshaller jaxb2Marshaller;

    @Override
    public List<Behandling> hentBehandlinger(String aktoerId) {
        return ((BehandlingerResponse) jaxb2Marshaller.unmarshal(new StreamSource(getClass().getResourceAsStream("/mockdata/behandlinger.xml")))).getBehandlinger();
    }
}
