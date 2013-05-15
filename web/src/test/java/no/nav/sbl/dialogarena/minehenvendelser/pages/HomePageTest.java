package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.CmsContentRetriever;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import static org.mockito.Mockito.when;

public class HomePageTest extends AbstractWicketTest {

    private BehandlingService behandlingServiceMock;
    private AktoerIdService aktoerIdServiceMock;
    private Kodeverk kodeverkServiceMock;

    @Override
    protected void setup() {
        behandlingServiceMock = mock(BehandlingService.class);
        aktoerIdServiceMock = mock(AktoerIdService.class);
        kodeverkServiceMock = mock(Kodeverk.class);
        mock(CmsContentRetriever.class);
        mock("footerLinks", Map.class);
        mock("navigasjonslink", "");
        mock("dokumentInnsendingBaseUrl", "");
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

        wicketTester.goTo(HomePage.class)
                .should().containComponent(withId("behandlingerUnderArbeid").and(ofType(PropertyListView.class))).should().containLabelsSaying(testTittel2, testTittel1);
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

}
