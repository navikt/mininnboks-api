package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "dokumentforventning")
public class Dokumentforventning {

    @XmlElement
    private String skjemaId;
    @XmlElement
    private String egendefinertTittel;
    @XmlElement
    private String innsendingsvalg;
    @XmlElement
    private boolean hovedskjema;

    public String getSkjemaId() {
        return skjemaId;
    }

    public String getEgendefinertTittel() {
        return egendefinertTittel;
    }

    public String getInnsendingsvalg() {
        return innsendingsvalg;
    }

    public boolean isHovedskjema() {
        return hovedskjema;
    }

    public boolean erInnsendt() {
        return innsendingsvalg.equals("DIGITAL");
    }
}
