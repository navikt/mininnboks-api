package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandlig;

import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class BehandlingDTO {

	private String brukerBehandlingsId;
	private Behandlingsstatus status;
	private String hovedkravskjemaId;
	private String behandligstype;
	private DateTime sistEndret;
	private DateTime innsendtDato;
	private List<DokumentforventningDTO> dokumentforventninger;

	public enum Behandlingsstatus {
		UNDER_ARBEID, FERDIG
	};

	public String getBrukerBehandlingsId() {
		return brukerBehandlingsId;
	}

	public Behandlingsstatus getStatus() {
		return status;
	}

	public static Builder getBuilder() {
		return new Builder();
	}

	public String getHovedkravskjemaId() {
		return hovedkravskjemaId;
	}

	public String getBehandligstype() {
		return behandligstype;
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

	public static Transformer<BehandlingDTO, Behandlingsstatus> STATUS = new Transformer<BehandlingDTO, Behandlingsstatus>() {
		@Override
		public Behandlingsstatus transform(BehandlingDTO behandlingDTO) {
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
		
		public Builder status(Behandlingsstatus status){
			dto.status = status;
			return this;
		}
		
		public Builder hovedkravskjemaId(String id){
			dto.hovedkravskjemaId = id;
			return this;
		}
		
		public Builder behandlingsType(String type){
			dto.behandligstype = type;
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
