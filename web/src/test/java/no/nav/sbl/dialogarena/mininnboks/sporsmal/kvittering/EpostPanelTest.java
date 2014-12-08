package no.nav.sbl.dialogarena.mininnboks.sporsmal.kvittering;

import no.nav.sbl.dialogarena.mininnboks.WicketPageTest;
import no.nav.sbl.dialogarena.mininnboks.consumer.EpostService;
import no.nav.sbl.dialogarena.mininnboks.config.EpostServiceMockContext;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration(classes = {EpostServiceMockContext.class})
public class EpostPanelTest extends WicketPageTest {

    private static final String HAR_EPOST_ID = "harEpostContainer";
    private static final String MANGLER_EPOST_ID = "manglerEpostContainer";
    private static final String TPS_UTILGJENGELIG_ID = "tpsUtilgjengeligContainer";

    @Inject
    EpostService epostService;

    @Test
    public void viserRiktigContainerHvisServicenReturnererEpostadresse() throws Exception {
        when(epostService.hentEpostadresse()).thenReturn("test@example.com");
        wicketTester.goToPageWith(EpostPanel.class);

        wicketTester
                .should().containComponent(thatIsVisible().withId(HAR_EPOST_ID))
                .should().containComponent(thatIsInvisible().withId(MANGLER_EPOST_ID))
                .should().containComponent(thatIsInvisible().withId(TPS_UTILGJENGELIG_ID));
    }

    @Test
    public void viserRiktigContainerHvisServicenReturnererTomStreng() throws Exception {
        when(epostService.hentEpostadresse()).thenReturn("");
        wicketTester.goToPageWith(EpostPanel.class);

        wicketTester
                .should().containComponent(thatIsInvisible().withId(HAR_EPOST_ID))
                .should().containComponent(thatIsVisible().withId(MANGLER_EPOST_ID))
                .should().containComponent(thatIsInvisible().withId(TPS_UTILGJENGELIG_ID));
    }

    @Test
    public void viserRiktigContainerHvisServicenKasterException() throws Exception {
        when(epostService.hentEpostadresse()).thenThrow(new Exception("Noe gikk galt i TPS"));
        wicketTester.goToPageWith(EpostPanel.class);

        wicketTester
                .should().containComponent(thatIsInvisible().withId(HAR_EPOST_ID))
                .should().containComponent(thatIsInvisible().withId(MANGLER_EPOST_ID))
                .should().containComponent(thatIsVisible().withId(TPS_UTILGJENGELIG_ID));
    }
}