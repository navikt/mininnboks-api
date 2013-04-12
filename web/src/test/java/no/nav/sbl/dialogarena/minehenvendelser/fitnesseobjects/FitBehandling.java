package no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;

import java.util.List;

public class FitBehandling {

    public String aktorId;

    public String behandlingsId;
    public String dokumentbehandlingtype;
    public String brukerbehandlingType;
    public String status;
    public String sistEndretDato;
    public String innsendtDato;
    public List<String> kodeverkId;
    public List<String> innsendingsvalg;
    public List<Boolean> hovedskjema;
    public List<String> egendefinertTittel;

    public Behandling asBehandling() {
        Behandling behandling = new Behandling();

        return behandling;
    }

}
