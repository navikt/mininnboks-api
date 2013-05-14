package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBehandlingsstatus;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummeringer;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.transformToDokumentforventing;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkServiceMock.KODEVERK_ID_1;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkServiceMock.KODEVERK_ID_2;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkServiceMock.KODEVERK_ID_3;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkServiceMock.KODEVERK_ID_4;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkServiceMock.KODEVERK_ID_5;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkServiceMock.KODEVERK_ID_6;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkServiceMock.KODEVERK_ID_7;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkServiceMock.KODEVERK_ID_8;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkServiceMock.KODEVERK_ID_9;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBehandlingsstatus.*;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandlingType.DOKUMENT_BEHANDLING;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandlingType.DOKUMENT_ETTERSENDING;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentbehandlingType.SOKNADSINNSENDING;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg.IKKE_VALGT;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg.LASTET_OPP;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg.SENDES_IKKE;

public class MockCreationUtil {

    public static Dokumentforventning createMock(boolean isHovedskjema, WSInnsendingsValg innsendingsValg) {
        WSDokumentForventningOppsummering wsDokumentForventning = new WSDokumentForventningOppsummering()
                .withHovedskjema(isHovedskjema)
                .withInnsendingsValg(innsendingsValg);
        return transformToDokumentforventing(wsDokumentForventning);
    }

    public static WSBrukerBehandlingOppsummering createWsBehandlingMock() {
        return createWsBehandlingMock(new DateTime(2013, 1, 2, 1, 1), new DateTime(2013, 1, 2, 1, 1), FERDIG)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer());
    }

    public static WSBrukerBehandlingOppsummering createWsBehandlingMock(DateTime innsendtDato, DateTime sistEndret, WSBehandlingsstatus status, boolean ettersending) {
        return new WSBrukerBehandlingOppsummering()
                .withStatus(status)
                .withBehandlingsId("DA01-000-000-029")
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(innsendtDato)
                .withSistEndret(sistEndret)
                .withBrukerBehandlingType(ettersending ? DOKUMENT_ETTERSENDING : DOKUMENT_BEHANDLING)
                .withDokumentbehandlingType(SOKNADSINNSENDING)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer());
    }

    public static WSBrukerBehandlingOppsummering createWsBehandlingMock(DateTime innsendtDato, DateTime sistEndret, WSBehandlingsstatus status) {
        return createWsBehandlingMock(innsendtDato, sistEndret, status, false);
    }

    public static WSDokumentForventningOppsummering createDokumentForventningMock(boolean hovedDok, String kodeverkId, WSInnsendingsValg innsendingsValg) {
        return new WSDokumentForventningOppsummering()
                .withKodeverkId(kodeverkId)
                .withInnsendingsValg(innsendingsValg)
                .withHovedskjema(hovedDok);
    }

    public static WSBrukerBehandlingOppsummering createFerdigBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 2, 1, 1), new DateTime(2013, 1, 2, 1, 1), FERDIG, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_2, LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_3, SENDES_IKKE));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandlingOppsummering createFerdigBehandlingMedAlleInnsendt() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 2, 1, 1), new DateTime(2013, 1, 2, 1, 1), FERDIG, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_2, LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_3, LASTET_OPP));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandlingOppsummering createFerdigBehandlingMedIngenInnsendt() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 2, 1, 1), new DateTime(2013, 1, 2, 1, 1), FERDIG, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, SENDES_IKKE),
                createDokumentForventningMock(false, KODEVERK_ID_2, SENDES_IKKE),
                createDokumentForventningMock(false, KODEVERK_ID_3, SENDES_IKKE));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandlingOppsummering createFerdigEttersendingBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 3, 1, 1), new DateTime(2013, 1, 3, 1, 1), FERDIG, true);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_5, LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_4, LASTET_OPP).withFriTekst("Egendefinert tekst"),
                createDokumentForventningMock(false, KODEVERK_ID_6, SENDES_IKKE));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandlingOppsummering createUnderArbeidBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 4, 1, 1), new DateTime(2013, 1, 4, 1, 1), UNDER_ARBEID, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_7, IKKE_VALGT),
                createDokumentForventningMock(false, KODEVERK_ID_8, IKKE_VALGT));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandlingOppsummering createUnderArbeidBehandling(DateTime innsendtDato, String hovedSkjemaId) {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(innsendtDato, innsendtDato, UNDER_ARBEID, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, hovedSkjemaId, LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_2, LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_3, SENDES_IKKE));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandlingOppsummering createUnderArbeidEttersendingBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 5, 1, 1), new DateTime(2013, 1, 5, 1, 1), UNDER_ARBEID, true);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, IKKE_VALGT),
                createDokumentForventningMock(false, KODEVERK_ID_9, IKKE_VALGT));
        return wsBehandlingMock;
    }

    public static List<WSBrukerBehandlingOppsummering> createFitnesseTestData() {
        List<WSBrukerBehandlingOppsummering> behandlinger = new ArrayList<>();
        WSBrukerBehandlingOppsummering behandling1 = createWsBehandlingMock(null, new DateTime(2012, 9, 19, 1, 18), UNDER_ARBEID);
        WSBrukerBehandlingOppsummering behandling2 = createWsBehandlingMock(null, new DateTime(2012, 9, 19, 1, 18), UNDER_ARBEID);

        behandling1.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_2, SENDES_IKKE)
        );

        behandling2.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_2, LASTET_OPP)
        );

        behandlinger.add(behandling1);
        behandlinger.add(behandling2);
        return behandlinger;
    }


}
