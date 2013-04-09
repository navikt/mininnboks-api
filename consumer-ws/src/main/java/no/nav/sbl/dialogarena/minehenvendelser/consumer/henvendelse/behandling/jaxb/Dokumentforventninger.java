package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class Dokumentforventninger implements Serializable {

    @XmlElement(name = "dokumentforventning")
    private List<Dokumentforventning> dokumentforventningList;

    public List<Dokumentforventning> getDokumentforventningList() {
        if (dokumentforventningList == null) {
            dokumentforventningList = new ArrayList<>();
        }
        return dokumentforventningList;
    }
}
