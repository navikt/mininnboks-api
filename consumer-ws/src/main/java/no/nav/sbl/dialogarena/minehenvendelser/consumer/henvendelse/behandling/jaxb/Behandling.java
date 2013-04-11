package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

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
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning.HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning.STATUS_INNSENDT;
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
        return fetchInnsendteDokumenter().size();
    }

    public int getAntallSubDokumenter() {
        return fetchAlleDokumenter().size();
    }


    public List<Dokumentforventning> fetchInnsendteDokumenter() {
        return on(dokumentforventninger)
                .filter(where(STATUS_INNSENDT, equalTo(true)))
                .filter(where(HOVEDSKJEMA, equalTo(false)))
                .collect();
    }

    public List<Dokumentforventning> fetchAlleDokumenter() {
        return on(dokumentforventninger)
                .filter(where(HOVEDSKJEMA, equalTo(false)))
                .collect();
    }

    public Dokumentforventning fetchHoveddokument() {
        return on(dokumentforventninger)
                .filter(where(HOVEDSKJEMA, equalTo(true)))
                .head().get();
    }

    public static final Transformer<Behandling, String> STATUS = new Transformer<Behandling, String>() {
        @Override
        public String transform(Behandling behandling) {
            return behandling.getStatus();
        }
    };

}
