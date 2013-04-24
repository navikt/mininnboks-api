package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventning;
import org.apache.commons.collections15.Transformer;

import java.io.Serializable;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.util.KodeverkOppslag.hentKodeverk;
import static org.apache.commons.collections15.TransformerUtils.stringValueTransformer;

/**
 * Domeneobjekt som representerer en dokumentforventning
 */
public final class Dokumentforventning implements Serializable {

    public enum Innsendingsvalg {IKKE_VALGT, SEND_SENERE, LASTET_OPP, SEND_I_POST, SENDES_AV_ANDRE, SENDES_IKKE};

    private String kodeverkId;
    private Innsendingsvalg innsendingsvalg;
    private boolean hovedskjema;
    private String friTekst;

    private Dokumentforventning() { }

    private static Transformer<WSDokumentForventning, Dokumentforventning> dokumentforventningTransformer = new Transformer<WSDokumentForventning, Dokumentforventning>() {

        @Override
        public Dokumentforventning transform(WSDokumentForventning wsDokumentForventning) {
            Dokumentforventning dokumentforventning = new Dokumentforventning();
            dokumentforventning.friTekst = optional(wsDokumentForventning.getFriTekst()).map(stringValueTransformer()).getOrElse(null);

            dokumentforventning.hovedskjema =  wsDokumentForventning.isHovedskjema();

            switch (wsDokumentForventning.getInnsendingsValg()){
                case LASTET_OPP: dokumentforventning.innsendingsvalg = Innsendingsvalg.LASTET_OPP; break;
                case SENDES_IKKE: dokumentforventning.innsendingsvalg = Innsendingsvalg.SENDES_IKKE; break;
                case IKKE_VALGT: dokumentforventning.innsendingsvalg = Innsendingsvalg.IKKE_VALGT; break;
                case SEND_I_POST: dokumentforventning.innsendingsvalg = Innsendingsvalg.SEND_I_POST; break;
                case SEND_SENERE: dokumentforventning.innsendingsvalg = Innsendingsvalg.SEND_SENERE; break;
                case SENDES_AV_ANDRE: dokumentforventning.innsendingsvalg = Innsendingsvalg.SENDES_AV_ANDRE; break;
                default: throw new ApplicationException("Ukjent verdi for innsendingsvalg");
            }

          //  dokumentforventning.innsendingsvalg = wsDokumentForventning.getInnsendingsValg();


            return dokumentforventning;
        }
    };

    public static Dokumentforventning transformToDokumentforventing(WSDokumentForventning wsDokumentForventning) {
        return dokumentforventningTransformer.transform(wsDokumentForventning);
    }

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
