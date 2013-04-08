package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.adapter.DateTimeAdapterXml;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Behandling" , namespace = "http://service.provider.henvendelse.dialogarena.sbl.nav.no")
public class BehandlingDTO {

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
    private List<DokumentforventningDTO> dokumentforventninger;

    public static final String UNDER_ARBEID = "UNDER_ARBEID";
    public static final String FERDIG= "FERDIG";


	public String getBrukerBehandlingsId() {
		return brukerBehandlingsId;
	}

	public String getStatus() {
		return status;
	}

	public static Builder getBuilder() {
		return new Builder();
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

	public List<DokumentforventningDTO> getDokumentforventninger() {
		if(dokumentforventninger == null){
			dokumentforventninger = new ArrayList<>();
		}
		return dokumentforventninger;
	}

	public static Transformer<BehandlingDTO, String> STATUS = new Transformer<BehandlingDTO, String>() {
		@Override
		public String transform(BehandlingDTO behandlingDTO) {
			return behandlingDTO.getStatus();
		}
	};

	public static class  Builder {

		BehandlingDTO dto;
		
		private Builder(){
			dto = new BehandlingDTO();
		}
		
		public BehandlingDTO create() {
			return dto;
		}
		
		public Builder brukerBehandlingsId(String id){
			dto.brukerBehandlingsId = id;
			return this;
		}
		
		public Builder status(String status){
			dto.status = status;
			return this;
		}
		
		public Builder hovedkravskjemaId(String id){
			dto.hovedkravskjemaId = id;
			return this;
		}
		
		public Builder behandlingsType(String type){
			dto.behandlingstype = type;
			return this;
		}
		
		public Builder sistEndret(DateTime sistEndret){
			dto.sistEndret = sistEndret;
			return this;
		}
		
		public Builder innsendtDato(DateTime innsendtDato){
			dto.innsendtDato= innsendtDato;
			return this;
		}
		
		public Builder dokumentForventninger(List<DokumentforventningDTO> dokumentforventningDTOs){
			dto.dokumentforventninger = new ArrayList<>();
			dto.dokumentforventninger.addAll(dokumentforventningDTOs);
			return this;
		}
		
	}
	
}
