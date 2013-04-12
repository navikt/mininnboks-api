package no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;

import java.util.List;

public class FitBehandling {

    public String behandlingsId;
    public String dokumentbehandlingtype;
    public String status;
    public String sistEndretDato;
    public String innsendtDato;
    public List<String> kodeverksIdDokumenter;
    public List<String> innsendingsValg;
    public List<String> fritekst;

    public Behandling asBehandling() {
        Behandling behandling = new Behandling();

        return behandling;
    }

}
