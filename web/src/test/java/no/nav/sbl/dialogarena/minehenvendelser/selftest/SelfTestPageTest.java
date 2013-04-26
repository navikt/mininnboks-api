package no.nav.sbl.dialogarena.minehenvendelser.selftest;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.CmsContentRetriever;
import no.nav.sbl.dialogarena.minehenvendelser.pages.AbstractWicketTest;

import org.junit.Test;

public class SelfTestPageTest extends AbstractWicketTest {

    @Override
    protected void setup() {
        mock(CmsContentRetriever.class);
    }

    @Test
    public void shouldRenderSelfTestPage() {
        wicketTester.goTo(SelfTestPage.class).should().containComponent(withId("serviceStatusTable"));
    }

}
