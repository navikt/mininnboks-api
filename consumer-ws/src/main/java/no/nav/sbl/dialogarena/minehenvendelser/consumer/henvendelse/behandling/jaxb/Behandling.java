package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.adapter.DateTimeAdapterXml;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning.HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning.STATUS_LASTET_OPP;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.util.KodeverkOppslag.hentKodeverk;

public class Behandling implements Serializable {

    private String behandlingsId;

    private String hovedskjemaId;

    public enum BrukerbehandlingType {DOKUMENT_BEHANDLING};

    public enum DokumentbehandlingType {SOKNADSINNSENDING, ETTERSENDING};

    public enum Behandlingsstatus {AVBRUTT_AV_BRUKER, IKKE_SPESIFISERT, UNDER_ARBEID, FERDIG};

    private DateTime sistEndret;

    private DateTime innsendtDato;

    private BrukerbehandlingType behandlingstype;

    private DokumentbehandlingType dokumentbehandlingType;

	private Behandlingsstatus status;

    private List<Dokumentforventning> dokumentforventninger = new ArrayList<>();

	public String getBehandlingsId() {
		return behandlingsId;
	}

    public BrukerbehandlingType getBehandlingstype() {
		return behandlingstype;
	}

    public DokumentbehandlingType getDokumentbehandlingType() {
        return dokumentbehandlingType;
    }

    public Behandlingsstatus getStatus() {
        return status;
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
        return hentKodeverk(fetchHoveddokument().getKodeverkId());
    }

    public int getAntallInnsendteDokumenter() {
        return fetchInnsendteDokumenter().size();
    }

    public int getAntallSubDokumenter() {
        return fetchAlleDokumenter().size();
    }

    public List<Dokumentforventning> fetchInnsendteDokumenter() {
        return on(dokumentforventninger)
                .filter(where(STATUS_LASTET_OPP, equalTo(true)))
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

    public static final Transformer<Behandling, Behandlingsstatus> STATUS = new Transformer<Behandling, Behandlingsstatus>() {
        @Override
        public Behandlingsstatus transform(Behandling behandling) {
            return behandling.getStatus();
        }
    };

}
