package no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

public class FitBehandling {

    private static final Logger logger = LoggerFactory.getLogger(FitBehandling.class);

    public String aktorId;

    public String behandlingsId;
    public String dokumentbehandlingType;
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
        populateSimpleFields(behandling);
        populateFieldsFromLists(behandling);
        logger.info("Behandling created from fitnesse object, toString as follows: " + behandling.toString());
        return behandling;
    }

    private void populateFieldsFromLists(Behandling behandling) {
        setInternalState(behandling, "behandlingsId", behandlingsId);
        setInternalState(behandling, "dokumentbehandlingType", dokumentbehandlingType);
        setInternalState(behandling, "brukerbehandlingType", brukerbehandlingType);
        setInternalState(behandling, "status", status);
        setInternalState(behandling, "sistEndretDato", sistEndretDato);
        setInternalState(behandling, "innsendtDato", innsendtDato);
    }

    private void populateSimpleFields(Behandling behandling) {
        List<Dokumentforventning> dokumentforventninger = new ArrayList<>();
        for (int i = 0; i < kodeverkId.size(); i++) {
            dokumentforventninger.add(createNewDokumentForventningBasedOnLists(i));
        }
        setInternalState(behandling, "dokumentforventninger", dokumentforventninger);
    }

    private Dokumentforventning createNewDokumentForventningBasedOnLists(int indexOfLists) {
        Dokumentforventning dokumentforventning = new Dokumentforventning();
        setInternalState(dokumentforventning, "kodeverkId", kodeverkId.get(indexOfLists));
        setInternalState(dokumentforventning, "innsendingsvalg", innsendingsvalg.get(indexOfLists));
        setInternalState(dokumentforventning, "hovedskjema", hovedskjema.get(indexOfLists));
        setInternalState(dokumentforventning, "egendefinertTittel", egendefinertTittel.get(indexOfLists));
        return dokumentforventning;
    }

}
