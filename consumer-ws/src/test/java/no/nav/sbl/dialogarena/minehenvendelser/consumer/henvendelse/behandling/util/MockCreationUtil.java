package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.KodeverkServiceMock;
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

    public static final String KODEVERK_ID_1 = "kodeForDagpenger";
    public static final String KODEVERK_ID_2 = "kodeForPermitteringsvarsel";
    public static final String KODEVERK_ID_3 = "kodeForArbeidsavtale";
    public static final String KODEVERK_ID_4 = "kodeForEgetVedlegg";
    public static final String KODEVERK_ID_5 = "kodeForForeldrepenger";
    public static final String KODEVERK_ID_6 = "kodeForInntektsopplysninger";
    public static final String KODEVERK_ID_7 = "kodeForKontantstoette";
    public static final String KODEVERK_ID_8 = "kodeForOvergangsstoenad";
    public static final String KODEVERK_ID_9 = "kodeForAvtaleOmDeltBosted";

    public static void insertKodeverk(KodeverkServiceMock kodeverk) {
        kodeverk.insertKodeverk(KODEVERK_ID_1, "Søknad om dagpenger");
        kodeverk.insertKodeverk(KODEVERK_ID_2, "Permitteringsvarsel");
        kodeverk.insertKodeverk(KODEVERK_ID_3, "Arbeidsavtale");
        kodeverk.insertKodeverk(KODEVERK_ID_4, "Annet: ");
        kodeverk.insertKodeverk(KODEVERK_ID_5, "Søknad om foreldrepenger");
        kodeverk.insertKodeverk(KODEVERK_ID_6, "Inntektsopplysninger");
        kodeverk.insertKodeverk(KODEVERK_ID_7, "Søknad om kontantstøtte");
        kodeverk.insertKodeverk(KODEVERK_ID_8, "Stønad om Overgangsstønad");
        kodeverk.insertKodeverk(KODEVERK_ID_9, "Avtale om delt bosted");
    }

    public static Dokumentforventning createMock(boolean isHovedskjema, WSInnsendingsValg innsendingsValg) {
        WSDokumentForventningOppsummering wsDokumentForventning = new WSDokumentForventningOppsummering()
                .withHovedskjema(isHovedskjema)
                .withInnsendingsValg(innsendingsValg);
        Dokumentforventning dokumentforventning = Dokumentforventning.transformToDokumentforventing(wsDokumentForventning);
        return dokumentforventning;
    }

    public static WSBrukerBehandling createWsBehandlingMock() {
        return createWsBehandlingMock(new DateTime(2013, 1, 2, 1, 1), new DateTime(2013, 1, 2, 1, 1), WSBehandlingsstatus.FERDIG)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer());
    }

    public static WSBrukerBehandling createWsBehandlingMock(DateTime innsendtDato, DateTime sistEndret, WSBehandlingsstatus status, boolean ettersending) {
        return new WSBrukerBehandling()
                .withStatus(status)
                .withBehandlingsId("behandlingId")
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(innsendtDato)
                .withSistEndret(sistEndret)
                .withDokumentbehandlingType(ettersending ? WSDokumentbehandlingType.ETTERSENDING : WSDokumentbehandlingType.SOKNADSINNSENDING)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer());
    }

    public static WSBrukerBehandling createWsBehandlingMock(DateTime innsendtDato, DateTime sistEndret, WSBehandlingsstatus status) {
        return createWsBehandlingMock(innsendtDato, sistEndret, status, false);
    }

    public static WSDokumentForventningOppsummering createDokumentForventningMock(boolean hovedDok, String kodeverkId, WSInnsendingsValg innsendingsValg) {
        return new WSDokumentForventningOppsummering().withKodeverkId(kodeverkId).withInnsendingsValg(innsendingsValg).withHovedskjema(hovedDok);
    }

    public static WSBrukerBehandling createFerdigBehandling() {
        WSBrukerBehandling wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 2, 1, 1), new DateTime(2013, 1, 2, 1, 1), WSBehandlingsstatus.FERDIG, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_2, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_3, WSInnsendingsValg.SENDES_IKKE));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandling createFerdigEttersendingBehandling() {
        WSBrukerBehandling wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 3, 1, 1), new DateTime(2013, 1, 3, 1, 1), WSBehandlingsstatus.FERDIG, true);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_5, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_4, WSInnsendingsValg.LASTET_OPP).withFriTekst("Egendefinert tekst"),
                createDokumentForventningMock(false, KODEVERK_ID_6, WSInnsendingsValg.SENDES_IKKE));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandling createUnderArbeidBehandling() {
        WSBrukerBehandling wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 4, 1, 1), new DateTime(2013, 1, 4, 1, 1), WSBehandlingsstatus.UNDER_ARBEID, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_7, WSInnsendingsValg.IKKE_VALGT),
                createDokumentForventningMock(false, KODEVERK_ID_8, WSInnsendingsValg.IKKE_VALGT));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandling createUnderArbeidEttersendingBehandling() {
        WSBrukerBehandling wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 5, 1, 1), new DateTime(2013, 1, 5, 1, 1), WSBehandlingsstatus.UNDER_ARBEID, true);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, WSInnsendingsValg.IKKE_VALGT),
                createDokumentForventningMock(false, KODEVERK_ID_9, WSInnsendingsValg.IKKE_VALGT));
        return wsBehandlingMock;
    }

    public static List<WSBrukerBehandling> createFitnesseTestData() {
        List<WSBrukerBehandling> behandlinger = new ArrayList<>();
        WSBrukerBehandling behandling1 = createWsBehandlingMock(null, new DateTime(2012, 9, 19, 1, 18), WSBehandlingsstatus.UNDER_ARBEID);
        WSBrukerBehandling behandling2 = createWsBehandlingMock(null, new DateTime(2012, 9, 19, 1, 18), WSBehandlingsstatus.UNDER_ARBEID);

        behandling1.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_2, WSInnsendingsValg.SENDES_IKKE)
        );

        behandling2.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_2, WSInnsendingsValg.LASTET_OPP)
        );

        behandlinger.add(behandling1);
        behandlinger.add(behandling2);
        return behandlinger;
    }


}
