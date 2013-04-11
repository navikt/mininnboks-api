package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import org.apache.commons.collections15.Transformer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.util.KodeverkOppslag.hentKodeverk;

@XmlRootElement(name = "dokumentforventning")
public class Dokumentforventning implements Serializable {

    public static final String STATUS_DIGITAL = "STATUS_DIGITAL";

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
        return STATUS_DIGITAL.equals(innsendingsvalg);
    }

    public String getTittel() {
        return hentKodeverk(getSkjemaId());
    }

    public static final Transformer<Dokumentforventning, Boolean> DOKUMENTFORVENTNING_STATUS = new Transformer<Dokumentforventning, Boolean>() {
        @Override
        public Boolean transform(Dokumentforventning dokumentforventning) {
            return dokumentforventning.erInnsendt();
        }
    };

    public static final Transformer<Dokumentforventning, Boolean> HOVEDSKJEMA = new Transformer<Dokumentforventning, Boolean>() {
        @Override
        public Boolean transform(Dokumentforventning dokumentforventning) {
            return dokumentforventning.isHovedskjema();
        }
    };
}
