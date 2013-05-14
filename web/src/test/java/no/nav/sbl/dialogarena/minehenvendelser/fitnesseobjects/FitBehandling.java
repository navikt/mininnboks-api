package no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBehandlingsstatus;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandlingType;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummeringer;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentbehandlingType;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HentBrukerBehandlingerResponse;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class FitBehandling {

    private static final String EMPTY_PLACEHOLDER = "blank";

    public String aktorId;
    public String behandlingsId;
    public String dokumentbehandlingtype;
    public String brukerbehandlingType;
    public String status;
    public String hovedkravskjemaId;
    public String sistEndretDato;
    public String innsendtDato;

    public List<String> kodeverkId;
    public List<String> innsendingsvalg;
    public List<String> hovedskjema;
    public List<String> egendefinertTittel;

    public HentBrukerBehandlingerResponse asHentBrukerBehandlingResponse(){
        return new HentBrukerBehandlingerResponse().withBrukerBehandlinger(createBrukerBehandling());
    }

    public WSBrukerBehandlingOppsummering asBrukerBehandling(){
        return createBrukerBehandling();
    }

    private WSBrukerBehandlingOppsummering createBrukerBehandling() {
        WSBrukerBehandlingOppsummering behandling = new WSBrukerBehandlingOppsummering();
        behandling.withBehandlingsId(behandlingsId);
        behandling.withStatus(WSBehandlingsstatus.fromValue(status));
        behandling.withDokumentbehandlingType(WSDokumentbehandlingType.fromValue(dokumentbehandlingtype));
        behandling.withBrukerBehandlingType(WSBrukerBehandlingType.fromValue(brukerbehandlingType));
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
        dokumentforventning.withHovedskjema(hovedskjema.get(indexOfLists).equals("JA") ? true : false);
        dokumentforventning.withInnsendingsValg(WSInnsendingsValg.fromValue(innsendingsvalg.get(indexOfLists)));
        dokumentforventning.withKodeverkId(kodeverkId.get(indexOfLists));
        if (!egendefinertTittel.get(indexOfLists).equals(EMPTY_PLACEHOLDER)) {
            dokumentforventning.withFriTekst(egendefinertTittel.get(indexOfLists));
        }
        return dokumentforventning;
    }

}
