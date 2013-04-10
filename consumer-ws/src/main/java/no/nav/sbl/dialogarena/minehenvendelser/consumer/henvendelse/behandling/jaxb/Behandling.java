package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.adapter.DateTimeAdapterXml;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.util.KodeverkOppslag.hentKodeverk;

@XmlRootElement(name = "Behandling" , namespace = "http://service.provider.henvendelse.dialogarena.sbl.nav.no")
public class Behandling implements Serializable {

    private static final int HOVEDDOKUMENT = 1;
    public static final String UNDER_ARBEID = "UNDER_ARBEID";
    public static final String FERDIG= "FERDIG";

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

    @XmlElement
    private Dokumentforventninger dokumentforventninger;

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
        if (dokumentforventninger == null) {
            dokumentforventninger = new Dokumentforventninger();
        }
		return dokumentforventninger.getDokumentforventningList();
	}

	public static final Transformer<Behandling, String> STATUS = new Transformer<Behandling, String>() {
		@Override
		public String transform(Behandling behandling) {
			return behandling.getStatus();
		}
	};

    public String getTittel() {
        return hentKodeverk(this.getHovedkravskjemaId());
    }

    public int getAntallInnsendteDokumenter() {
        int antallFerdige = 0;
        for (Dokumentforventning dok : Dokumentforventninger.getInnsendtedokumenterWhichAreNotHovedskjemaer(this.getDokumentforventninger())) {
            if (dok.erInnsendt()) {
                antallFerdige++;
            }
        }
        return antallFerdige;
    }

    public int getAntallSubDokumenter() {
        return  dokumentforventninger.getDokumentforventningList().size() - HOVEDDOKUMENT;
    }
}
