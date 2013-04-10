package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;

@XmlRootElement
public class Dokumentforventninger implements Serializable {

    private static final boolean ER_INNSENDT = true;
    private static final boolean IKKE_INNSENDT = false;
    private static final boolean NOT_HOVEDSKJEMA = false;

    @XmlElement(name = "dokumentforventning")
    private List<Dokumentforventning> dokumentforventningList;

    public List<Dokumentforventning> getDokumentforventningList() {
        if (dokumentforventningList == null) {
            dokumentforventningList = new ArrayList<>();
        }
        return dokumentforventningList;
    }

    public static List<Dokumentforventning> getInnsendtedokumenterWhichAreNotHovedskjemaer(List<Dokumentforventning> dokumentforventningList) {
        return on(dokumentforventningList)
                .filter(where(Dokumentforventning.STATUS, equalTo(ER_INNSENDT)))
                .filter(where(Dokumentforventning.HOVEDSKJEMA, equalTo(NOT_HOVEDSKJEMA))).collect();
    }

    public static List<Dokumentforventning> getIkkeInnsendtedokumenterWhichAreNotHovedskjemaer(List<Dokumentforventning> dokumentforventningList) {
        return on(dokumentforventningList)
                .filter(where(Dokumentforventning.STATUS, equalTo(IKKE_INNSENDT)))
                .filter(where(Dokumentforventning.HOVEDSKJEMA, equalTo(NOT_HOVEDSKJEMA))).collect();
    }

    public static List<Dokumentforventning> getDokumenterWhichAreNotHovedskjemaer(List<Dokumentforventning> dokumentforventningList) {
        return on(dokumentforventningList).filter(where(Dokumentforventning.HOVEDSKJEMA, equalTo(NOT_HOVEDSKJEMA))).collect();
    }

}
