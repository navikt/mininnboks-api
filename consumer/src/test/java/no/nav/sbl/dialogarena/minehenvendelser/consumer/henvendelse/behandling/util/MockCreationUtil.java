package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBehandlingsstatus;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventningOppsummeringer;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingsstegtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.datatype.DatatypeFactory.newInstance;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.transformToDokumentforventing;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBehandlingsstatus.UNDER_ARBEID;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingType.DOKUMENT_BEHANDLING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingType.DOKUMENT_ETTERSENDING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentbehandlingType.SOKNADSINNSENDING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.IKKE_VALGT;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.LASTET_OPP;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.SENDES_IKKE;


public class MockCreationUtil {

    public static final String AKTOR_ID = "***REMOVED***";

    public static final String KODEVERK_ID_1 = "NAV 00-01.00";
    public static final String KODEVERK_ID_2 = "NAV 76-08.03";
    public static final String KODEVERK_ID_3 = "NAV 00-02.00";
    public static final String KODEVERK_ID_7 = "NAV 34-00.08";
    public static final String KODEVERK_ID_8 = "NAV 15-00.01";
    public static final String KODEVERK_ID_9 = "NAV 76-13.16";

    public static Dokumentforventning createDokumentforventning(boolean isHovedskjema, WSInnsendingsValg innsendingsValg) {
        WSDokumentForventningOppsummering wsDokumentForventning = new WSDokumentForventningOppsummering()
                .withHovedskjema(isHovedskjema)
                .withInnsendingsValg(innsendingsValg);
        return transformToDokumentforventing(wsDokumentForventning);
    }

    public static WSDokumentForventningOppsummering createWSDokumentForventningMock(boolean hovedDok, String kodeverkId, WSInnsendingsValg innsendingsValg) {
        return new WSDokumentForventningOppsummering()
                .withKodeverkId(kodeverkId)
                .withInnsendingsValg(innsendingsValg)
                .withHovedskjema(hovedDok);
    }

    public static WSBrukerBehandlingOppsummering createWsBehandlingMock() {
        return createWsBehandlingMock(new DateTime(2013, 1, 2, 1, 1), new DateTime(2013, 1, 2, 1, 1), WSBehandlingsstatus.UNDER_ARBEID)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer())
                .withHovedskjemaId("hovedSkjemaId-1");
    }

    public static WSBrukerBehandlingOppsummering createWsBehandlingMock(DateTime innsendtDato, DateTime sistEndret, WSBehandlingsstatus status, boolean ettersending) {
        return new WSBrukerBehandlingOppsummering()
                .withStatus(status)
                .withBehandlingsId("behandlingsId-1")
                .withHovedskjemaId("hovedSkjemaId-1")
                .withInnsendtDato(innsendtDato)
                .withSistEndret(sistEndret)
                .withBrukerBehandlingType(ettersending ? DOKUMENT_ETTERSENDING : DOKUMENT_BEHANDLING)
                .withDokumentbehandlingType(SOKNADSINNSENDING)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer());
    }

    public static WSBrukerBehandlingOppsummering createWsBehandlingMock(DateTime innsendtDato, DateTime sistEndret, WSBehandlingsstatus status) {
        return createWsBehandlingMock(innsendtDato, sistEndret, status, false);
    }

    public static WSBrukerBehandlingOppsummering createUnderArbeidBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 4, 1, 1), new DateTime(2013, 1, 4, 1, 1), UNDER_ARBEID, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createWSDokumentForventningMock(true, KODEVERK_ID_7, IKKE_VALGT),
                createWSDokumentForventningMock(false, KODEVERK_ID_8, IKKE_VALGT));
        return wsBehandlingMock;
    }

    public static List<WSBrukerBehandlingOppsummering> createFitnesseTestData() {
        List<WSBrukerBehandlingOppsummering> behandlinger = new ArrayList<>();
        WSBrukerBehandlingOppsummering behandling1 = createWsBehandlingMock(null, new DateTime(2012, 9, 19, 1, 18), WSBehandlingsstatus.UNDER_ARBEID);
        WSBrukerBehandlingOppsummering behandling2 = createWsBehandlingMock(null, new DateTime(2012, 9, 19, 1, 18), WSBehandlingsstatus.UNDER_ARBEID);

        behandling1.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createWSDokumentForventningMock(true, KODEVERK_ID_1, LASTET_OPP),
                createWSDokumentForventningMock(false, KODEVERK_ID_2, SENDES_IKKE)
        );

        behandling2.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createWSDokumentForventningMock(true, KODEVERK_ID_1, LASTET_OPP),
                createWSDokumentForventningMock(false, KODEVERK_ID_2, LASTET_OPP)
        );

        behandlinger.add(behandling1);
        behandlinger.add(behandling2);
        return behandlinger;
    }

    public static WSBrukerBehandlingOppsummering createUnderArbeidEttersendingBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 5, 1, 1), new DateTime(2013, 1, 5, 1, 1), WSBehandlingsstatus.UNDER_ARBEID, true);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createWSDokumentForventningMock(true, KODEVERK_ID_1, WSInnsendingsValg.IKKE_VALGT),
                createWSDokumentForventningMock(false, KODEVERK_ID_9, WSInnsendingsValg.IKKE_VALGT));
        return wsBehandlingMock;
    }

    public static FinnSakOgBehandlingskjedeListeResponse createFinnSakOgBehandlingskjedeListeResponse(List<Behandlingskjede> behandlingskjede) {
        return new FinnSakOgBehandlingskjedeListeResponse()
                .withResponse(new no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse()
                        .withSak(new Sak().withBehandlingskjede(behandlingskjede)));
    }

    public static List<Behandlingskjede> populateFinnbehandlingKjedeList() {
        List<Behandlingskjede> behandlingsKjeder = new ArrayList<>();
        behandlingsKjeder.add(createFinnbehandlingKjede("Uførepensjon", KODEVERK_ID_1, true));
        behandlingsKjeder.add(createFinnbehandlingKjede("Sykepenger", KODEVERK_ID_2, true));
        behandlingsKjeder.add(createFinnbehandlingKjede("Arbeidsavklaringspenger", KODEVERK_ID_3, true));
        behandlingsKjeder.add(createFinnbehandlingKjede("Uførepensjon", KODEVERK_ID_1, false));
        behandlingsKjeder.add(createFinnbehandlingKjede("Sykepenger", KODEVERK_ID_2, false));
        return behandlingsKjeder;
    }

    public static Behandlingskjede createDummyBehandlingkjede() {
        return new Behandlingskjede()
                .withNormertBehandlingstid(new Behandlingstid().withType(new Behandlingstidtyper()))
                .withStartNAVtid(createXmlGregorianDate(1, 1, 2013))
                .withBehandlingskjedetype(new Behandlingskjedetyper())
                .withBehandlingskjedeId("id")
                .withKjedensNAVfrist(createXmlGregorianDate(11, 11, 2022))
                .withSisteBehandlingREF("sisteBehandlingref-1")
                .withSisteBehandlingsstegREF("sisteBehandlingsStegRef-1")
                .withSisteBehandlingsstegtype(new Behandlingsstegtyper().withValue("behandlingsStegValue").withKodeRef("behandlingsStegKodeRef").withKodeverksRef(KODEVERK_ID_1));
    }

    private static Behandlingskjede createFinnbehandlingKjede(String value, String kodeverkRef, boolean isFerdig) {
        Behandlingskjede behandlingskjede = new Behandlingskjede()
                .withStartNAVtid(createXmlGregorianDate(1, 1, 2013))
                .withNormertBehandlingstid(new Behandlingstid().withTid(BigInteger.TEN).withType(new Behandlingstidtyper()))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withValue(value).withKodeverksRef(kodeverkRef));
        if (isFerdig) {
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
}
