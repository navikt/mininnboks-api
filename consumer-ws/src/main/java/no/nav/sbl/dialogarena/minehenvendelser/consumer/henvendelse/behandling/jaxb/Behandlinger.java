package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Behandlinger", namespace = "http://service.provider.henvendelse.dialogarena.sbl.nav.no")
public class Behandlinger {

    @XmlElement(name = "Behandling")
    private List<Behandling> behandlinger = new ArrayList<>();

    public List<Behandling> getBehandlinger() {
        return behandlinger;
    }
}
