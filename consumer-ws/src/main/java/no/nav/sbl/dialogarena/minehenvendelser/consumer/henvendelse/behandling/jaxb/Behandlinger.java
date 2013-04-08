package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Behandlinger", namespace = "http://service.provider.henvendelse.dialogarena.sbl.nav.no")
public class Behandlinger {

    @XmlElement(name = "Behandling")
    private List<Behandling> behandlinger;

    public List<Behandling> getBehandlingerList() {
        if(behandlinger == null){
            behandlinger = new ArrayList<>();
        }
        return behandlinger;
    }
}
