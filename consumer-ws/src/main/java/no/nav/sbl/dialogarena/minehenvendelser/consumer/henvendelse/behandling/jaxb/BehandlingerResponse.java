package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "brukerbehandlingerResponse", namespace = "http://nav.no/tjeneste/virksomhet/henvendelse/v1/informasjon")
public class BehandlingerResponse {

    @XmlElement(name = "brukerbehandling")
    private List<Behandling> behandlinger = new ArrayList<>();

    public List<Behandling> getBehandlinger() {
        return behandlinger;
    }
}
