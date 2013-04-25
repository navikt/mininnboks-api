package no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HentBrukerBehandlingerResponse;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.transformToBehandling;
import static org.codehaus.plexus.util.StringUtils.isNotEmpty;
import static org.codehaus.plexus.util.StringUtils.repeat;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

public class FitBehandling {

    private static final Logger logger = LoggerFactory.getLogger(FitBehandling.class);

    private static final String EMPTY_PLACEHOLDER = "blank";

    public String aktorId;
    public String behandlingsId;
    public String dokumentbehandlingType;
    public String brukerbehandlingType;
    public String status;
    public String hovedkravskjemaId;
    public String sistEndretDato;
    public String innsendtDato;

    public List<String> kodeverkId;
    public List<String> innsendingsvalg;
    public List<Boolean> hovedskjema;
    public List<String> egendefinertTittel;

    public HentBrukerBehandlingerResponse asHentBrukerBehandlingResponse(){
        return new HentBrukerBehandlingerResponse().withBrukerBehandlinger(createNewBrukerBehandling());
    }

    private WSBrukerBehandling createNewBrukerBehandling() {
        WSBrukerBehandling behandling = new WSBrukerBehandling();
        behandling.withBehandlingsId(behandlingsId);
        behandling.withStatus(WSBehandlingsstatus.fromValue(status));
        if (isNotEmpty(sistEndretDato)) {
            behandling.withSistEndret(new DateTime(ISODateTimeFormat.dateTimeNoMillis().parseDateTime(sistEndretDato)));
        }
        if (isNotEmpty(innsendtDato)) {
            behandling.withInnsendtDato(new DateTime(ISODateTimeFormat.dateTimeNoMillis().parseDateTime(innsendtDato)));
        }
        behandling.withDokumentForventningOppsummeringer(createNewDokumentForventninger());
        return behandling;
    }

    private WSDokumentForventningOppsummeringer createNewDokumentForventninger() {
        WSDokumentForventningOppsummeringer oppsummeringer = new WSDokumentForventningOppsummeringer();
        for (int i = 0; i < kodeverkId.size(); i++) {
            oppsummeringer.getDokumentForventningOppsummering().add(createNewWSDokumentForventningOppsunmmeringBasedOnLists(i));
        }
        return oppsummeringer;
    }

    private WSDokumentForventningOppsummering createNewWSDokumentForventningOppsunmmeringBasedOnLists(int indexOfLists) {
        WSDokumentForventningOppsummering dokumentforventning = new WSDokumentForventningOppsummering();
        dokumentforventning.withHovedskjema(hovedskjema.get(indexOfLists));
        dokumentforventning.withInnsendingsValg(WSInnsendingsValg.fromValue(innsendingsvalg.get(indexOfLists)));
        dokumentforventning.withKodeverkId(kodeverkId.get(indexOfLists));
        if (!egendefinertTittel.get(indexOfLists).equals(EMPTY_PLACEHOLDER)) {
            dokumentforventning.withFriTekst(egendefinertTittel.get(indexOfLists));
        }
        return dokumentforventning;
    }

}
