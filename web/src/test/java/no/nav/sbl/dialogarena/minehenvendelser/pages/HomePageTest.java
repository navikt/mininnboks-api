package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.utils.UTF8Control;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.minehenvendelser.FoedselsnummerService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
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
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createUnderArbeidBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createUnderArbeidEttersendingBehandling;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

public class HomePageTest extends AbstractWicketTest {

    private static final String TEST_FNR = "***REMOVED***";
    private BehandlingService behandlingServiceMock;
    private FoedselsnummerService foedselsnummerServiceMock;
    private Kodeverk kodeverkServiceMock;

    @Override
    protected void setup() {
        behandlingServiceMock = mock(BehandlingService.class);
        foedselsnummerServiceMock = mock(FoedselsnummerService.class);
        when(foedselsnummerServiceMock.getFoedselsnummer()).thenReturn(TEST_FNR);
        kodeverkServiceMock = mock(Kodeverk.class);
        mock("footerLinks", Map.class);
        mock("navigasjonslink", "");
        mock("dokumentInnsendingBaseUrl", "");
        mock(TilbakemeldingService.class);
        mock(SakogbehandlingService.class);
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

    private void setupFakeCms() {
        ValueRetriever tekstValueRetriever = new ValueRetriever() {
            private ResourceBundle appBundle = ResourceBundle.getBundle("content/innholdstekster", new Locale("nb"), new UTF8Control());
            private ResourceBundle webkomponenterBundle = ResourceBundle.getBundle("content/sbl-webkomponenter", new Locale("nb"), new UTF8Control());

            @Override
            public String getValueOf(String key, String language) {
                return appBundle.containsKey(key) ? appBundle.getString(key) : webkomponenterBundle.getString(key);
            }
        };
        CmsContentRetriever innholdstekster = new CmsContentRetriever();
        innholdstekster.setTeksterRetriever(tekstValueRetriever);
        innholdstekster.setDefaultLocale("no");
        applicationContext.putBean(innholdstekster);
    }

}
