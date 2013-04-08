package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.adapter.DateTimeAdapterXml;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@XmlRootElement(name = "Behandling" , namespace = "http://service.provider.henvendelse.dialogarena.sbl.nav.no")
public class Behandling {

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

    public static final String UNDER_ARBEID = "UNDER_ARBEID";
    public static final String FERDIG= "FERDIG";


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
		return dokumentforventninger.getDokumentforventningList();
	}

	public static final Transformer<Behandling, String> STATUS = new Transformer<Behandling, String>() {
		@Override
		public String transform(Behandling behandling) {
			return behandling.getStatus();
		}
	};

    public String getTittel() {
        return "Tittel fra kodeverk";
    }

    public int getAntallFerdigeDokumenter() {
        int antallFerdige = 0;
        for (Dokumentforventning dok : dokumentforventninger.getDokumentforventningList()) {
            if(dok.isHovedskjema()) {
                continue;
            }
            if(dok.erInnsendt()) {
                antallFerdige++;
            }
        }
        return antallFerdige;
    }

    public int getAntallSubDokumenter() {
        return  dokumentforventninger.getDokumentforventningList().size() - 1;
    }
}
