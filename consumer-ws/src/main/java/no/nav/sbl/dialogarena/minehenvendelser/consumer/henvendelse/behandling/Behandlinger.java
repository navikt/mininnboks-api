package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Behandlinger", namespace = "http://service.provider.henvendelse.dialogarena.sbl.nav.no")
public class Behandlinger {

    @XmlElement(name = "Behandling")
    private List<BehandlingDTO> behandlingDTOs;

    public List<BehandlingDTO> getBehandlingDTOs() {
        if(behandlingDTOs == null){
            behandlingDTOs = new ArrayList<>();
        }
        return behandlingDTOs;
    }
}
