package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import no.nav.modig.lang.collections.predicate.TransformerOutputPredicate;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.adapter.DateTimeAdapterXml;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning.DOKUMENTFORVENTNING_STATUS;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning.HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.util.KodeverkOppslag.hentKodeverk;

@XmlRootElement(name = "Behandling" , namespace = "http://service.provider.henvendelse.dialogarena.sbl.nav.no")
public class Behandling implements Serializable {

    public static final String UNDER_ARBEID = "UNDER_ARBEID";
    public static final String FERDIG = "FERDIG";
    public static final boolean IS_INNSENDT = true;
    public static final boolean NOT_INNSENDT = false;
    public static final boolean IS_HOVEDSKJEMA = true;
    public static final boolean NOT_HOVEDSKJEMA = false;

    @XmlElement
	private String brukerBehandlingsId;

    @XmlElement
	private String status;

    @XmlElement
    private String hovedkravskjemaId;

    @XmlElement
    private String behandlingstype;

    @XmlElement
    @XmlJavaTypeAdapter(DateTimeAdapterXml.class)
    private DateTime sistEndret;

    @XmlElement
    @XmlJavaTypeAdapter(DateTimeAdapterXml.class)
    private DateTime innsendtDato;

    @XmlElementWrapper(name="dokumentforventninger")
    @XmlElement(name = "dokumentforventning")
    private List<Dokumentforventning> dokumentforventninger = new ArrayList<>();

	public String getBrukerBehandlingsId() {
		return brukerBehandlingsId;
	}

	public String getStatus() {
		return status;
	}

	public String getHovedkravskjemaId() {
		return hovedkravskjemaId;
	}

	public String getBehandlingstype() {
		return behandlingstype;
	}

	public DateTime getSistEndret() {
		return sistEndret;
	}

	public DateTime getInnsendtDato() {
		return innsendtDato;
	}

	public List<Dokumentforventning> getDokumentforventninger() {
		return dokumentforventninger;
	}

    public String getTittel() {
        return hentKodeverk(getHovedkravskjemaId());
    }

    public int getAntallInnsendteDokumenter() {
        return filterDokumenter(IS_INNSENDT, NOT_HOVEDSKJEMA).size();
    }

    public int getAntallSubDokumenter() {
        return filterDokumenter(null, NOT_HOVEDSKJEMA).size();
    }

    /**
     * @param isInnsendt Om innsendte eller ikke innsendte dokumenter skal med i svaret. null betyr alle skal med
     * @param isHovedskjema Om hovedskjema skal med i svaret eller ikke. null betyr alle skal med
     * @return Filtrert liste
     */
    public List<Dokumentforventning> filterDokumenter(Boolean isInnsendt, Boolean isHovedskjema) {
        TransformerOutputPredicate<Dokumentforventning, Boolean> innsendtFilter = where(DOKUMENTFORVENTNING_STATUS, equalTo(isInnsendt));
        TransformerOutputPredicate<Dokumentforventning, Boolean> hovedskjemaFilter = where(HOVEDSKJEMA, equalTo(isHovedskjema));
        TransformerOutputPredicate<Dokumentforventning, Boolean> alle = where(ALWAYS_TRUE_TRANSFORMER, equalTo(true));

        return on(dokumentforventninger)
                .filter(isInnsendt == null ? alle : innsendtFilter)
                .filter(isHovedskjema == null ? alle : hovedskjemaFilter)
                .collect();
    }

    public static final Transformer<Behandling, String> BEHANDLING_STATUS = new Transformer<Behandling, String>() {
        @Override
        public String transform(Behandling behandling) {
            return behandling.getStatus();
        }
    };

    private static final Transformer<Dokumentforventning, Boolean> ALWAYS_TRUE_TRANSFORMER = new Transformer<Dokumentforventning, Boolean>() {
        @Override
        public Boolean transform(Dokumentforventning dokumentforventning) {
            return true;
        }
    };

}
