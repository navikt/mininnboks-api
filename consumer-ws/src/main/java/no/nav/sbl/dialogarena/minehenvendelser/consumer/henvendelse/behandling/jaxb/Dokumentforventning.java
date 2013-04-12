package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import org.apache.commons.collections15.Transformer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.util.KodeverkOppslag.hentKodeverk;

@XmlRootElement(name = "dokumentforventning")
public class Dokumentforventning implements Serializable {

    public enum Innsendingsvalg {IKKE_VALGT, SEND_SENERE, LASTET_OPP, SEND_I_POST, SENDES_AV_ANDRE, SENDES_IKKE};

    @XmlElement
    private String kodeverkId;
    @XmlElement
    private Innsendingsvalg innsendingsvalg;
    @XmlElement
    private boolean hovedskjema;
    @XmlElement
    private String egendefinertTittel;

    public String getKodeverkId() {
        return kodeverkId;
    }

    public Innsendingsvalg getInnsendingsvalg() {
        return innsendingsvalg;
    }

    public boolean isHovedskjema() {
        return hovedskjema;
    }

    public String getEgendefinertTittel() {
        return egendefinertTittel;
    }

    public boolean erInnsendt() {
        return Innsendingsvalg.LASTET_OPP.equals(innsendingsvalg);
    }

    public String getTittel() {
        return hentKodeverk(getKodeverkId());
    }

    public static final Transformer<Dokumentforventning, Boolean> STATUS_INNSENDT = new Transformer<Dokumentforventning, Boolean>() {
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
