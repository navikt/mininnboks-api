package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.minehenvendelser.FoedselsnummerService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.sbl.dialogarena.webkomponent.tilbakemelding.service.TilbakemeldingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBehandlingsstatus;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.transformToBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_1;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_2;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_3;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_9;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createDummyBehandlingkjede;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createWSDokumentForventningMock;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createWsBehandlingMock;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad.transformToSoeknad;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBehandlingsstatus.UNDER_ARBEID;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.IKKE_VALGT;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.LASTET_OPP;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.SENDES_IKKE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

public class HomePageTest extends AbstractWicketTest {

    private static final String TEST_FNR = "***REMOVED***";
    private BehandlingService behandlingServiceMock;
    private FoedselsnummerService foedselsnummerServiceMock;
    private Kodeverk kodeverkServiceMock;
    private SakogbehandlingService sakogbehandlingService;

    @Override
    protected void setup() {
        behandlingServiceMock = mock(BehandlingService.class);
        foedselsnummerServiceMock = mock(FoedselsnummerService.class);
        sakogbehandlingService = mock(SakogbehandlingService.class);
        when(foedselsnummerServiceMock.getFoedselsnummer()).thenReturn(TEST_FNR);
        kodeverkServiceMock = mock(Kodeverk.class);
        mock("footerLinks", Map.class);
        mock("navigasjonslink", "");
        mock("dokumentInnsendingBaseUrl", "");
        mock(TilbakemeldingService.class);
        applicationContext.putBean("tilbakemeldingEnabled", true);

        setupFakeCms();
    }

    @Test
    public void renderHomePage() {
        wicketTester.goTo(HomePage.class)
                .should().containComponent(withId("behandlingerUnderArbeid").and(ofType(PropertyListView.class)));
    }

    @Test
    public void renderHomePageWithNotSentBehandling() {
        String testTittel1 = KODEVERK_ID_1;
        List<Behandling> behandlinger = createListWithOneNotSent();
        when(behandlingServiceMock.hentBehandlinger(TEST_FNR)).thenReturn(behandlinger);
        when(kodeverkServiceMock.getTittel(testTittel1)).thenReturn(testTittel1);

        Component behandlingerUnderArbeid = wicketTester.goTo(HomePage.class).get().component(withId("behandlingerUnderArbeid"));
        List<Component> labels = wicketTester.get().components(withId("tittel").and(ofType(Label.class)).and(containedInComponent(equalTo(behandlingerUnderArbeid))));

        assertThat(labels.get(0).getDefaultModelObjectAsString(), equalTo(testTittel1));
    }

    @Test
    public void renderHomePageWithSortedViewOfNotSentBehandling() {
        String testTittel1 = KODEVERK_ID_1;
        String testTittel2 = KODEVERK_ID_2;
        List<Behandling> behandlinger = createListWithTwoNotSent();
        when(behandlingServiceMock.hentBehandlinger(TEST_FNR)).thenReturn(behandlinger);
        when(kodeverkServiceMock.getTittel(testTittel1)).thenReturn(testTittel1);
        when(kodeverkServiceMock.getTittel(testTittel2)).thenReturn(testTittel2);

        Component behandlingerUnderArbeid = wicketTester.goTo(HomePage.class).get().component(withId("behandlingerUnderArbeid"));

        List<Component> labels = wicketTester.get().components(withId("tittel").and(ofType(Label.class)).and(containedInComponent(equalTo(behandlingerUnderArbeid))));
        assertThat(labels.get(0).getDefaultModelObjectAsString(), equalTo(testTittel2));
        assertThat(labels.get(1).getDefaultModelObjectAsString(), equalTo(testTittel1));
    }

    @Test
    public void homePageShouldContainSoeknaderUnderArbeidListView() {
        wicketTester.goTo(HomePage.class)
                .should().containComponent(withId("soeknaderUnderArbeid"));
    }

    @Test
    public void soeknaderUnderArbeidListViewShouldBeVisibleWhenSoeknaderExist() {
        when(foedselsnummerServiceMock.getFoedselsnummer()).thenReturn(TEST_FNR);
        when(sakogbehandlingService.finnSoeknaderUnderArbeid(TEST_FNR)).thenReturn(asList(transformToSoeknad(createDummyBehandlingkjede())));
        wicketTester.goTo(HomePage.class)
                .should().containComponent(withId("tema"))
                .should().containComponent(withId("beskrivelse"))
                .should().containComponent(withId("detaljer")); //.and(ofType(Link.class)));
    }

    @Test
    public void renderHomePageWithViewofNotSentEtterbehandling() {
        String testFnr = "svein";
        String testTittel1 = KODEVERK_ID_1;
        List<Behandling> behandlinger = createListWithOneNotSentEttersending();
        when(foedselsnummerServiceMock.getFoedselsnummer()).thenReturn(testFnr);
        when(behandlingServiceMock.hentBehandlinger(testFnr)).thenReturn(behandlinger);
        when(kodeverkServiceMock.getTittel(testTittel1)).thenReturn(testTittel1);

        Component behandlingerUnderArbeid = wicketTester.goTo(HomePage.class).get().component(withId("behandlingerUnderArbeid"));
        List<Component> labels = wicketTester.get().components(withId("tittel").and(ofType(Label.class)).and(containedInComponent(equalTo(behandlingerUnderArbeid))));

        assertThat(labels.get(0).getDefaultModelObjectAsString(), equalTo("Ettersendelse til " + testTittel1));
    }

    private List<Behandling> createListWithOneNotSent() {
        List<Behandling> behandlinger = new ArrayList<>();
        behandlinger.add(transformToBehandling(createUnderArbeidBehandling(new DateTime(2010, 1, 1, 12, 0), KODEVERK_ID_1)));
        return behandlinger;
    }

    private List<Behandling> createListWithOneNotSentEttersending() {
        List<Behandling> behandlinger = new ArrayList<>();
        behandlinger.add(transformToBehandling(createUnderArbeidEttersendingBehandling()));
        return behandlinger;
    }

    private List<Behandling> createListWithTwoNotSent() {
        List<Behandling> behandlinger = new ArrayList<>();
        behandlinger.add(transformToBehandling(createUnderArbeidBehandling(new DateTime(2010, 1, 1, 12, 0), KODEVERK_ID_1)));
        behandlinger.add(transformToBehandling(createUnderArbeidBehandling(new DateTime(2012, 1, 1, 12, 0), KODEVERK_ID_2)));
        return behandlinger;
    }

    private static WSBrukerBehandlingOppsummering createUnderArbeidBehandling(DateTime innsendtDato, String hovedSkjemaId) {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(innsendtDato, innsendtDato, UNDER_ARBEID, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createWSDokumentForventningMock(true, hovedSkjemaId, LASTET_OPP),
                createWSDokumentForventningMock(false, KODEVERK_ID_2, LASTET_OPP),
                createWSDokumentForventningMock(false, KODEVERK_ID_3, SENDES_IKKE));
        return wsBehandlingMock;
    }

    private static WSBrukerBehandlingOppsummering createUnderArbeidEttersendingBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 5, 1, 1), new DateTime(2013, 1, 5, 1, 1), WSBehandlingsstatus.UNDER_ARBEID, true);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createWSDokumentForventningMock(true, KODEVERK_ID_1, IKKE_VALGT),
                createWSDokumentForventningMock(false, KODEVERK_ID_9, IKKE_VALGT));
        return wsBehandlingMock;
    }
}
