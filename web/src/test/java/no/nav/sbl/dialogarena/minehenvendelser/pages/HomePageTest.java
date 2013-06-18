package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.utils.UTF8Control;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.webkomponent.tilbakemelding.service.TilbakemeldingService;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.transformToBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_1;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_2;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_5;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFerdigBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFerdigEttersendingBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createUnderArbeidBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createUnderArbeidEttersendingBehandling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

public class HomePageTest extends AbstractWicketTest {

    private BehandlingService behandlingServiceMock;
    private AktoerIdService aktoerIdServiceMock;
    private Kodeverk kodeverkServiceMock;

    @Override
    protected void setup() {
        behandlingServiceMock = mock(BehandlingService.class);
        aktoerIdServiceMock = mock(AktoerIdService.class);
        when(aktoerIdServiceMock.getAktoerId()).thenReturn("svein");
        kodeverkServiceMock = mock(Kodeverk.class);
        mock("footerLinks", Map.class);
        mock("navigasjonslink", "");
        mock("dokumentInnsendingBaseUrl", "");
        mock(TilbakemeldingService.class);
        applicationContext.putBean("tilbakemeldingEnabled", true);

        setupFakeCms();
    }

    @Test
    public void shouldRenderHomePage() {
        wicketTester.goTo(HomePage.class)
                .should().containComponent(withId("behandlingerUnderArbeid").and(ofType(PropertyListView.class)))
                .should().containComponent(withId("behandlingerFerdig").and(ofType(PropertyListView.class)));
    }

    @Test
    public void shouldRenderHomePageWithViewOfCompleteBehandlingAndNotSentBehandling() {
        String testAktoerId = "svein";
        String testTittel1 = KODEVERK_ID_1;
        String testTittel2 = KODEVERK_ID_2;
        List<Behandling> behandlinger = createListWithOneCompleteAndOneNotSent();
        when(aktoerIdServiceMock.getAktoerId()).thenReturn(testAktoerId);
        when(behandlingServiceMock.hentBehandlinger(testAktoerId)).thenReturn(behandlinger);
        when(kodeverkServiceMock.getTittel(testTittel1)).thenReturn(testTittel1);
        when(kodeverkServiceMock.getTittel(testTittel2)).thenReturn(testTittel2);

        wicketTester.goTo(HomePage.class)
                .should().containComponent(withId("behandlingerUnderArbeid").and(ofType(PropertyListView.class))).should().containLabelsSaying(testTittel1)
                .should().containComponent(withId("behandlingerFerdig").and(ofType(PropertyListView.class))).should().containLabelsSaying(testTittel2);
    }

    @Test
    public void shouldRenderHomePageWithSortedViewOfNotSentBehandling() {
        String testAktoerId = "svein";
        String testTittel1 = KODEVERK_ID_1;
        String testTittel2 = KODEVERK_ID_2;
        List<Behandling> behandlinger = createListWithTwoNotSent();
        when(aktoerIdServiceMock.getAktoerId()).thenReturn(testAktoerId);
        when(behandlingServiceMock.hentBehandlinger(testAktoerId)).thenReturn(behandlinger);
        when(kodeverkServiceMock.getTittel(testTittel1)).thenReturn(testTittel1);
        when(kodeverkServiceMock.getTittel(testTittel2)).thenReturn(testTittel2);

        Component behandlingerUnderArbeid = wicketTester.goTo(HomePage.class).get().component(withId("behandlingerUnderArbeid"));

        List<Component> labels = wicketTester.get().components(withId("tittel").and(ofType(Label.class)).and(containedInComponent(equalTo(behandlingerUnderArbeid))));
        assertThat(labels.get(0).getDefaultModelObjectAsString(), equalTo(testTittel2));
        assertThat(labels.get(1).getDefaultModelObjectAsString(), equalTo(testTittel1));
    }

    @Test
    public void shouldRenderHomePageWithViewofCompleteEttersendingBehandlingAndNotSentEtterbehandlingBehandling() {
        String testAktoerId = "svein";
        String testTittel1 = KODEVERK_ID_5;
        String testTittel2 = KODEVERK_ID_1;
        List<Behandling> behandlinger = createListWithOneCompleteEttersendingAndOneNotSentEttersending();
        when(aktoerIdServiceMock.getAktoerId()).thenReturn(testAktoerId);
        when(behandlingServiceMock.hentBehandlinger(testAktoerId)).thenReturn(behandlinger);
        when(kodeverkServiceMock.getTittel(testTittel1)).thenReturn(testTittel1);
        when(kodeverkServiceMock.getTittel(testTittel2)).thenReturn(testTittel2);


        wicketTester.goTo(HomePage.class)
                .should().containComponent(withId("behandlingerUnderArbeid").and(ofType(PropertyListView.class))).should().containLabelsSaying("Ettersendelse til " + testTittel1)
                .should().containComponent(withId("behandlingerFerdig").and(ofType(PropertyListView.class))).should().containLabelsSaying("Ettersendelse til " + testTittel2);
    }

    private List<Behandling> createListWithOneCompleteAndOneNotSent() {
        List<Behandling> behandlinger = new ArrayList<>();
        behandlinger.add(transformToBehandling(createFerdigBehandling()));
        behandlinger.add(transformToBehandling(createUnderArbeidBehandling()));
        return behandlinger;
    }

    private List<Behandling> createListWithOneCompleteEttersendingAndOneNotSentEttersending() {
        List<Behandling> behandlinger = new ArrayList<>();
        behandlinger.add(transformToBehandling(createFerdigEttersendingBehandling()));
        behandlinger.add(transformToBehandling(createUnderArbeidEttersendingBehandling()));
        return behandlinger;
    }

    private List<Behandling> createListWithTwoNotSent() {
        List<Behandling> behandlinger = new ArrayList<>();
        behandlinger.add(transformToBehandling(createUnderArbeidBehandling(new DateTime(2010, 1, 1, 12, 0), KODEVERK_ID_1)));
        behandlinger.add(transformToBehandling(createUnderArbeidBehandling(new DateTime(2012, 1, 1, 12, 0), KODEVERK_ID_2)));
        return behandlinger;
    }

    private void setupFakeCms() {
        ValueRetriever tekstValueRetriever = new ValueRetriever() {
            private ResourceBundle bundle = ResourceBundle.getBundle("content/innholdstekster", new Locale("nb"), new UTF8Control());

            @Override
            public String getValueOf(String key, String language) {
                return bundle.getString(key);
            }
        };
        CmsContentRetriever innholdstekster = new CmsContentRetriever();
        innholdstekster.setTeksterRetriever(tekstValueRetriever);
        innholdstekster.setDefaultLocale("no");
        applicationContext.putBean(innholdstekster);
    }

}
