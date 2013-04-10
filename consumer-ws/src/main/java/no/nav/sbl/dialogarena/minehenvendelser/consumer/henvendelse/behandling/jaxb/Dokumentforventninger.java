package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import no.nav.modig.lang.collections.predicate.TransformerOutputPredicate;
import org.apache.commons.collections15.Transformer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning.HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning.STATUS;

@XmlRootElement
public class Dokumentforventninger implements Serializable {

    public static final boolean IS_INNSENDT = true;
    public static final boolean NOT_INNSENDT = false;
    public static final boolean IS_HOVEDSKJEMA = true;
    public static final boolean NOT_HOVEDSKJEMA = false;

    @XmlElement(name = "dokumentforventning")
    private List<Dokumentforventning> dokumentforventningList;

    public List<Dokumentforventning> getDokumentforventningList() {
        if (dokumentforventningList == null) {
            dokumentforventningList = new ArrayList<>();
        }
        return dokumentforventningList;
    }
    
    /**
     * 
     * @param dokumentforventningList Liste som skal filtreres
     * @param isInnsendt Om innsendte eller ikke innsendte dokumenter skal med i svaret. null betyr alle skal med
     * @param isHovedskjema Om hovedskjema skal med i svaret eller ikke. null betyr alle skal med
     * @return Filtrert liste
     */
    public static List<Dokumentforventning> filterDokumenter(List<Dokumentforventning> dokumentforventningList, Boolean isInnsendt, Boolean isHovedskjema) {
        TransformerOutputPredicate<Dokumentforventning, Boolean> innsendtFilter = where(STATUS, equalTo(isInnsendt));
        TransformerOutputPredicate<Dokumentforventning, Boolean> allInnsendte = where(ALWAYS_TRUE_TRANSFORMER, equalTo(true));
        TransformerOutputPredicate<Dokumentforventning, Boolean> hovedskjemaFilter = where(HOVEDSKJEMA, equalTo(isHovedskjema));
        TransformerOutputPredicate<Dokumentforventning, Boolean> allSkjemas = where(ALWAYS_TRUE_TRANSFORMER, equalTo(true));

        return on(dokumentforventningList)
                .filter(isInnsendt == null ? allInnsendte : innsendtFilter)
                .filter(isHovedskjema == null ? allSkjemas : hovedskjemaFilter)
                .collect();
    }

    private static final Transformer<Dokumentforventning, Boolean> ALWAYS_TRUE_TRANSFORMER = new Transformer<Dokumentforventning, Boolean>() {

        @Override
        public Boolean transform(Dokumentforventning dokumentforventning) {
            return true;
        }
    };

}
