package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.CmsContentRetriever;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.transformToBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_1;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_2;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFerdigBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createUnderArbeidBehandling;
import static org.mockito.Mockito.when;

public class HomePageTest extends AbstractWicketTest {

    private BehandlingService behandlingServiceMock;
    private AktoerIdService aktoerIdServiceMock;
    private KodeverkService kodeverkServiceMock;

    @Override
    protected void setup() {
        behandlingServiceMock = mock(BehandlingService.class);
        aktoerIdServiceMock = mock(AktoerIdService.class);
        kodeverkServiceMock = mock(KodeverkService.class);
        mock(CmsContentRetriever.class);
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
        when(kodeverkServiceMock.hentKodeverk(testTittel1)).thenReturn(testTittel1);
        when(kodeverkServiceMock.hentKodeverk(testTittel2)).thenReturn(testTittel2);

        wicketTester.goTo(HomePage.class)
                .should().containComponent(withId("behandlingerUnderArbeid").and(ofType(PropertyListView.class))).should().containLabelsSaying(testTittel1)
                .should().containComponent(withId("behandlingerFerdig").and(ofType(PropertyListView.class))).should().containLabelsSaying(testTittel2);
    }

    private List<Behandling> createListWithOneCompleteAndOneNotSent() {
        List<Behandling> behandlinger = new ArrayList<>();
        behandlinger.add(transformToBehandling(createFerdigBehandling()));
        behandlinger.add(transformToBehandling(createUnderArbeidBehandling()));
        return behandlinger;
    }

}
