package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandlig;

import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.List;

public class BehandlingDTO {

    private String brukerBehandlingsId;
    private Behandlingsstatus status;
    private String hovedkravskjemaId;
    private String behandligstype;
    private DateTime sistEndret;
    private DateTime innsendtDato;
    private List<DokumentforventningDTO> dokumentforventninger;

    public enum Behandlingsstatus {UNDER_ARBEID, FERDIG};

    public String getBrukerBehandlingsId() {
        return brukerBehandlingsId;
    }

    public void setBrukerBehandlingsId(String brukerBehandlingsId) {
        this.brukerBehandlingsId = brukerBehandlingsId;
    }

    public Behandlingsstatus getStatus() {
        return status;
    }

    public void setStatus(Behandlingsstatus status) {
        this.status = status;
    }

    public static Transformer<BehandlingDTO, Behandlingsstatus> STATUS = new Transformer<BehandlingDTO, Behandlingsstatus>() {
        @Override
        public Behandlingsstatus transform(BehandlingDTO behandlingDTO) {
            return behandlingDTO.getStatus();
        }
    };
}
