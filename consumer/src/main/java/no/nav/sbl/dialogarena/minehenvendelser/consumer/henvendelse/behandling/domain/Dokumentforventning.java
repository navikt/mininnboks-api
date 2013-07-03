package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import org.apache.commons.collections15.Transformer;

import java.io.Serializable;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.Innsendingsvalg.LASTET_OPP;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.Innsendingsvalg.valueOf;
import static org.apache.commons.collections15.TransformerUtils.stringValueTransformer;

/**
 * Domeneobjekt som representerer en dokumentforventning
 */
public final class Dokumentforventning implements Serializable {

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

    private static Transformer<WSDokumentForventningOppsummering, Dokumentforventning> dokumentforventningTransformer = new Transformer<WSDokumentForventningOppsummering, Dokumentforventning>() {

        @Override
        public Dokumentforventning transform(WSDokumentForventningOppsummering wsDokumentForventningOppsummering) {
            Dokumentforventning dokumentforventning = new Dokumentforventning();
            dokumentforventning.friTekst = optional(wsDokumentForventningOppsummering.getFriTekst()).map(stringValueTransformer()).getOrElse(null);
            dokumentforventning.kodeverkId = wsDokumentForventningOppsummering.getKodeverkId();
            dokumentforventning.hovedskjema = wsDokumentForventningOppsummering.isHovedskjema();
            dokumentforventning.innsendingsvalg = valueOf(wsDokumentForventningOppsummering.getInnsendingsValg().value());
            return dokumentforventning;
        }

    };

    private String kodeverkId;
    private Innsendingsvalg innsendingsvalg;
    private boolean hovedskjema;
    private String friTekst;

    private Dokumentforventning() { }

    public static Dokumentforventning transformToDokumentforventing(WSDokumentForventningOppsummering wsDokumentForventningOppsummering) {
        return dokumentforventningTransformer.transform(wsDokumentForventningOppsummering);
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
        return LASTET_OPP.equals(innsendingsvalg);
    }

    public enum Innsendingsvalg {
        IKKE_VALGT,
        SEND_SENERE,
        LASTET_OPP,
        SEND_I_POST,
        SENDES_AV_ANDRE,
        SENDES_IKKE,
        INNSENDT;}

}
