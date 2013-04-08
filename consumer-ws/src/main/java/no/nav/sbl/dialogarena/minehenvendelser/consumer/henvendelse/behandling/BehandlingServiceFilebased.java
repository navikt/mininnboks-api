package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandlinger;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.inject.Inject;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

public class BehandlingServiceFilebased implements BehandlingService{

    @Inject
    private Jaxb2Marshaller jaxb2Marshaller;

    @Override
    public Behandlinger hentBehandlinger(String aktoerId) {
        InputStream inputStream = getClass().getResourceAsStream("/mockdata/behandlinger.xml");
        return (Behandlinger) jaxb2Marshaller.unmarshal(new StreamSource(inputStream));
    }
}
