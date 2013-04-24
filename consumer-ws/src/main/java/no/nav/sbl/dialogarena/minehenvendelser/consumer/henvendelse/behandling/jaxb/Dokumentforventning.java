package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import org.apache.commons.collections15.Transformer;

import java.io.Serializable;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.util.KodeverkOppslag.hentKodeverk;

public class Dokumentforventning implements Serializable {

    public enum Innsendingsvalg {IKKE_VALGT, SEND_SENERE, LASTET_OPP, SEND_I_POST, SENDES_AV_ANDRE, SENDES_IKKE};

    private String kodeverkId;
    private Innsendingsvalg innsendingsvalg;
    private boolean hovedskjema;
    private String friTekst;

    public String getKodeverkId() {
        return kodeverkId;
    }

    public Innsendingsvalg getInnsendingsvalg() {
        return innsendingsvalg;
    }

    public boolean isHovedskjema() {
        return hovedskjema;
    }

    public String getFriTekst() {
        return friTekst;
    }

    public boolean isLastetOpp() {
        return Innsendingsvalg.LASTET_OPP.equals(innsendingsvalg);
    }

    public String getTittel() {
        return hentKodeverk(getKodeverkId());
    }

    public static final Transformer<Dokumentforventning, Boolean> STATUS_LASTET_OPP = new Transformer<Dokumentforventning, Boolean>() {
        @Override
        public Boolean transform(Dokumentforventning dokumentforventning) {
            return dokumentforventning.isLastetOpp();
        }
    };

    public static final Transformer<Dokumentforventning, Boolean> HOVEDSKJEMA = new Transformer<Dokumentforventning, Boolean>() {
        @Override
        public Boolean transform(Dokumentforventning dokumentforventning) {
            return dokumentforventning.isHovedskjema();
        }
    };
}
