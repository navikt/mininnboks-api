package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBehandlingsstatus;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokument;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_1;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_9;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createWSDokumentForventningMock;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createWsBehandlingMock;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.IKKE_VALGT;

public class HenvendelsesBehandlingPortTypeMock implements HenvendelsesBehandlingPortType {

    @Override
    public WSDokument hentDokument(long l) {
        return new WSDokument();
    }

    @Override
    public WSDokumentForventning hentDokumentForventning(long l) {
        return  new WSDokumentForventning();
    }

    @Override
    public List<WSBrukerBehandlingOppsummering> hentBrukerBehandlingListe(String s) {
        return createBrukerBehandlingOppsumeringList();
    }

    @Override
    public boolean ping() {
        return true;
    }

    @Override
    public List<WSDokumentForventning> hentDokumentForventningListe(String s) {
        return new ArrayList<>();
    }

    @Override
    public WSBrukerBehandling hentBrukerBehandling(String s) {
        return new WSBrukerBehandling();
    }

    private static List<WSBrukerBehandlingOppsummering> createBrukerBehandlingOppsumeringList() {
        List<WSBrukerBehandlingOppsummering> oppsummeringer = new ArrayList<>();
        WSBrukerBehandlingOppsummering wsBrukerBehandlingOppsummering = createUnderArbeidEttersendingBehandling();
        wsBrukerBehandlingOppsummering.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(createDokumentForventingOppsummering());
        oppsummeringer.add(wsBrukerBehandlingOppsummering);
        return oppsummeringer;
    }

    private static WSBrukerBehandlingOppsummering createUnderArbeidEttersendingBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 5, 1, 1), new DateTime(2013, 1, 5, 1, 1), WSBehandlingsstatus.UNDER_ARBEID, true);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createWSDokumentForventningMock(true, KODEVERK_ID_1, IKKE_VALGT),
                createWSDokumentForventningMock(false, KODEVERK_ID_9, IKKE_VALGT));
        return wsBehandlingMock;
    }

    private static WSDokumentForventningOppsummering createDokumentForventingOppsummering() {
        return new WSDokumentForventningOppsummering()
                .withInnsendingsValg(WSInnsendingsValg.SEND_SENERE)
                .withFriTekst("texttextText")
                .withHovedskjema(true)
                .withKodeverkId(KODEVERK_ID_1);
    }
}
