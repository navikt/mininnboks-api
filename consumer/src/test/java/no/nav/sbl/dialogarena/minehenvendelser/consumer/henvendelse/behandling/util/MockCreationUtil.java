package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBehandlingsstatus;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventningOppsummeringer;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandling;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.BehandlingVS;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static javax.xml.datatype.DatatypeFactory.newInstance;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingType.DOKUMENT_BEHANDLING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingType.DOKUMENT_ETTERSENDING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentbehandlingType.SOKNADSINNSENDING;


public class MockCreationUtil {

    public static final String AKOTR_ID = "***REMOVED***";

    public static final String KODEVERK_ID_1 = "NAV 00-01.00";
    public static final String KODEVERK_ID_2 = "NAV 76-08.03";
    public static final String KODEVERK_ID_3 = "NAV 00-02.00";
    public static final String KODEVERK_ID_4 = "NAV 04-01.03";
    public static final String KODEVERK_ID_5 = "NAV 08-30.02";
    public static final String KODEVERK_ID_6 = "NAV 08-47.05";
    public static final String KODEVERK_ID_7 = "NAV 34-00.08";
    public static final String KODEVERK_ID_8 = "NAV 15-00.01";
    public static final String KODEVERK_ID_9 = "NAV 76-13.16";

    public static Dokumentforventning createMock(boolean isHovedskjema, WSInnsendingsValg innsendingsValg) {
        WSDokumentForventningOppsummering wsDokumentForventning = new WSDokumentForventningOppsummering()
                .withHovedskjema(isHovedskjema)
                .withInnsendingsValg(innsendingsValg);
        return Dokumentforventning.transformToDokumentforventing(wsDokumentForventning);
    }

    public static WSBrukerBehandlingOppsummering createWsBehandlingMock() {
        return createWsBehandlingMock(new DateTime(2013, 1, 2, 1, 1), new DateTime(2013, 1, 2, 1, 1), WSBehandlingsstatus.UNDER_ARBEID)
                .withDokumentForventningOppsummeringer(
                        new WSDokumentForventningOppsummeringer())
                .withHovedskjemaId("hovedSkjemaId");
    }

    private static WSDokumentForventningOppsummering createDokumentForventingOppsummering() {
        return new WSDokumentForventningOppsummering()
                .withInnsendingsValg(WSInnsendingsValg.SEND_SENERE)
                .withFriTekst("texttextText")
                .withHovedskjema(true)
                .withKodeverkId(KODEVERK_ID_1);
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
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 2, 1, 1), new DateTime(2013, 1, 2, 1, 1), WSBehandlingsstatus.FERDIG, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_2, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_3, WSInnsendingsValg.SENDES_IKKE));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandlingOppsummering createFerdigBehandlingMedAlleInnsendt() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 2, 1, 1), new DateTime(2013, 1, 2, 1, 1), WSBehandlingsstatus.FERDIG, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_2, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_3, WSInnsendingsValg.LASTET_OPP));
        return wsBehandlingMock;
    }
    public static WSBrukerBehandlingOppsummering createFerdigBehandlingMedIngenInnsendt() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 2, 1, 1), new DateTime(2013, 1, 2, 1, 1), WSBehandlingsstatus.FERDIG, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, WSInnsendingsValg.SENDES_IKKE),
                createDokumentForventningMock(false, KODEVERK_ID_2, WSInnsendingsValg.SENDES_IKKE),
                createDokumentForventningMock(false, KODEVERK_ID_3, WSInnsendingsValg.SENDES_IKKE));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandlingOppsummering createFerdigEttersendingBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 3, 1, 1), new DateTime(2013, 1, 3, 1, 1), WSBehandlingsstatus.FERDIG, true);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_5, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_4, WSInnsendingsValg.LASTET_OPP).withFriTekst("Egendefinert tekst"),
                createDokumentForventningMock(false, KODEVERK_ID_6, WSInnsendingsValg.SENDES_IKKE));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandlingOppsummering createUnderArbeidBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 4, 1, 1), new DateTime(2013, 1, 4, 1, 1), WSBehandlingsstatus.UNDER_ARBEID, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_7, WSInnsendingsValg.IKKE_VALGT),
                createDokumentForventningMock(false, KODEVERK_ID_8, WSInnsendingsValg.IKKE_VALGT));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandlingOppsummering createUnderArbeidBehandling(DateTime innsendtDato, String hovedSkjemaId) {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(innsendtDato, innsendtDato, WSBehandlingsstatus.UNDER_ARBEID, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, hovedSkjemaId, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_2, WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock(false, KODEVERK_ID_3, WSInnsendingsValg.SENDES_IKKE));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandlingOppsummering createUnderArbeidEttersendingBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 5, 1, 1), new DateTime(2013, 1, 5, 1, 1), WSBehandlingsstatus.UNDER_ARBEID, true);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock(true, KODEVERK_ID_1, WSInnsendingsValg.IKKE_VALGT),
                createDokumentForventningMock(false, KODEVERK_ID_9, WSInnsendingsValg.IKKE_VALGT));
        return wsBehandlingMock;
    }

    public static List<WSBrukerBehandlingOppsummering> createFitnesseTestData() {
        List<WSBrukerBehandlingOppsummering> behandlinger = new ArrayList<>();
        WSBrukerBehandlingOppsummering behandling1 = createWsBehandlingMock(null, new DateTime(2012, 9, 19, 1, 18), WSBehandlingsstatus.UNDER_ARBEID);
        WSBrukerBehandlingOppsummering behandling2 = createWsBehandlingMock(null, new DateTime(2012, 9, 19, 1, 18), WSBehandlingsstatus.UNDER_ARBEID);

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

    public static FinnSakOgBehandlingskjedeListeResponse createSakOgBehandlingskjedeListeResponse() {
        return new FinnSakOgBehandlingskjedeListeResponse()
                .withResponse(new no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse()
                        .withSak(createSak())
                );
    }

    public static HentBehandlingskjedensBehandlingerResponse createBehandlingskjedensBehandlingerResponse() {
        return new HentBehandlingskjedensBehandlingerResponse()
                .withBehandlingskjede(createHentBehandlingskjede());
    }

    public static HentBehandlingResponse createBehandlingResponse() {
        return new HentBehandlingResponse()
                .withBehandling(createHentBehandling(BigInteger.valueOf(99)));
    }

    private static Sak createSak() {
        return new Sak()
                .withBehandlingskjede(populateFinnbehandlingKjedeList());
    }

    private static List<Behandlingskjede> populateFinnbehandlingKjedeList() {
        List<Behandlingskjede> behandlingsKjeder = new ArrayList<>();
        behandlingsKjeder.add(createFinnbehandlingKjede("Uførepensjon", "MOCK-00-00-00", true));
        behandlingsKjeder.add(createFinnbehandlingKjede("Sykepenger", "MOCK-10-00-00", true));
        behandlingsKjeder.add(createFinnbehandlingKjede("Arbeidsavklaringspenger", "MOCK-20-00-00", true));
        behandlingsKjeder.add(createFinnbehandlingKjede("Uførepensjon", "MOCK-30-00-00", false));
        behandlingsKjeder.add(createFinnbehandlingKjede("Sykepenger", "MOCK-44-00-00", false));
        return behandlingsKjeder;
    }

    private static Behandlingskjede createFinnbehandlingKjede(String value, String kodeverkRef, boolean isFerdig) {
        Behandlingskjede behandlingskjede = new Behandlingskjede()
                .withStartNAVtid(createXmlGregorianDate(1, 1, 2013))
                .withNormertBehandlingstid(new Behandlingstid().withTid(BigInteger.TEN).withType(new Behandlingstidtyper()))
                .withBehandlingskjedetype(createBehandlingskjedetyper(value, kodeverkRef));

        if(isFerdig) {
           behandlingskjede.setSluttNAVtid(createXmlGregorianDate(1, 1, 2014));
        }

        return behandlingskjede;
    }

    private static XMLGregorianCalendar createXmlGregorianDate(int day, int month, int year) {
        DateTime dateTime = new DateTime().withDate(year, month, day);
        XMLGregorianCalendar xmlGregorianCalendar;
        try {
            xmlGregorianCalendar = newInstance().newXMLGregorianCalendar(dateTime.toGregorianCalendar());
        } catch (DatatypeConfigurationException e) {
            throw new ApplicationException("Failed to convert date to XMLGregorianCalendar ",e);
        }
        return xmlGregorianCalendar;
    }

    private static Behandlingskjedetyper createBehandlingskjedetyper(String value, String kodeverkRef) {
        return new Behandlingskjedetyper()
                .withValue(value)
                .withKodeverksRef(kodeverkRef);
    }

    private static no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.hentbehandlingskjedensbehandlinger.Behandlingskjede createHentBehandlingskjede() {
        return new no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.hentbehandlingskjedensbehandlinger.Behandlingskjede()
                .withBehandling(createHentBehandlinger());
    }

    private static Collection<Behandling> createHentBehandlinger() {
        List<Behandling> behandlinger = new ArrayList<>();
        behandlinger.add(createHentBehandling(BigInteger.valueOf(1)));
        behandlinger.add(createHentBehandling(BigInteger.valueOf(2)));
        behandlinger.add(createHentBehandling(BigInteger.valueOf(3)));
        behandlinger.add(createHentBehandling(BigInteger.valueOf(4)));
        return behandlinger;
    }


    private static Behandling createHentBehandling(BigInteger tid) {
        return new BehandlingVS()
                .withNormertBehandlingstid(new Behandlingstid().withTid(tid));
    }

    public static List<WSBrukerBehandlingOppsummering> createBrukerBehandlingOppsumeringList() {
        List<WSBrukerBehandlingOppsummering> oppsummeringer = new ArrayList<>();
        WSBrukerBehandlingOppsummering wsBrukerBehandlingOppsummering = createUnderArbeidEttersendingBehandling();
        wsBrukerBehandlingOppsummering.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(createDokumentForventingOppsummering());
        oppsummeringer.add(wsBrukerBehandlingOppsummering);
        oppsummeringer.add(createFerdigBehandling());
        oppsummeringer.add(createFerdigBehandlingMedIngenInnsendt());
        oppsummeringer.add(createFerdigBehandlingMedAlleInnsendt());
        return oppsummeringer;
    }
}
