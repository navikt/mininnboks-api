package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBehandlingsstatus;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummeringer;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentbehandlingType;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class MockCreationUtil {

    public static final String KODEVERK_ID_1 = "KODEVERK1";
    public static final String KODEVERK_ID_2 = "KODEVERK2";
    public static final String KODEVERK_ID_3 = "KODEVERK3";

    public static Dokumentforventning createMock(boolean isHovedskjema, WSInnsendingsValg innsendingsValg) {
        WSDokumentForventningOppsummering wsDokumentForventning = new WSDokumentForventningOppsummering()
                .withHovedskjema(isHovedskjema)
                .withInnsendingsValg(innsendingsValg);
        Dokumentforventning dokumentforventning = Dokumentforventning.transformToDokumentforventing(wsDokumentForventning);
        return dokumentforventning;
    }

    public static WSBrukerBehandling createWsBehandlingMock() {
        return createWsBehandlingMock(new DateTime(2013, 01, 01, 01, 01), new DateTime(2013, 01, 02, 01, 01), WSBehandlingsstatus.FERDIG)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer());
    }

    public static WSBrukerBehandling createWsBehandlingMock(DateTime innsendtDato, DateTime sistEndret, WSBehandlingsstatus status) {
        return new WSBrukerBehandling()
                .withStatus(status)
                .withBehandlingsId("behandlingId")
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(innsendtDato)
                .withSistEndret(sistEndret)
                .withDokumentbehandlingType(WSDokumentbehandlingType.SOKNADSINNSENDING)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer());
    }

    public static WSDokumentForventningOppsummering createDokumentForventningMock(boolean hovedDok, String kodeverkId, WSInnsendingsValg innsendingsValg) {
        return new WSDokumentForventningOppsummering().withKodeverkId(kodeverkId).withInnsendingsValg(innsendingsValg).withHovedskjema(hovedDok);
    }

    public static WSBrukerBehandling createFerdigBehandling() {
        WSBrukerBehandling wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 01, 01, 01, 01), new DateTime(2013, 01, 01, 01, 01), WSBehandlingsstatus.FERDIG);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_2, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_3, WSInnsendingsValg.SENDES_IKKE));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandling createUnderArbeidBehandling() {
        WSBrukerBehandling wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 01, 01, 01, 01), new DateTime(2013, 01, 01, 01, 01), WSBehandlingsstatus.UNDER_ARBEID);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, WSInnsendingsValg.IKKE_VALGT),
                createDokumentForventningMock(false, KODEVERK_ID_2, WSInnsendingsValg.IKKE_VALGT));
        return wsBehandlingMock;
    }

    public static List<WSBrukerBehandling> createFitnesseTestData(){
        List<WSBrukerBehandling> behandlinger = new ArrayList<>();
        WSBrukerBehandling behandling1 = createWsBehandlingMock(null, new DateTime(2012, 9, 19, 1, 18), WSBehandlingsstatus.UNDER_ARBEID);
        WSBrukerBehandling behandling2 = createWsBehandlingMock(null, new DateTime(2012, 9, 19, 1, 18), WSBehandlingsstatus.UNDER_ARBEID);

        behandling1.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, "kodeForDagpenger", WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false,"kodeForPermitteringsvarsel", WSInnsendingsValg.SENDES_IKKE)
        );

        behandling2.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, "kodeForDagpenger", WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false,"kodeForPermitteringsvarsel", WSInnsendingsValg.LASTET_OPP)
        );

        behandlinger.add(behandling1);
        behandlinger.add(behandling2);
        return behandlinger;
    }



}
