package no.nav.sbl.dialogarena.minehenvendelser.selftest;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import no.nav.sbl.dialogarena.minehenvendelser.config.WicketApplication;
import no.nav.sbl.dialogarena.minehenvendelser.pages.AbstractWicketTest;

import org.junit.Test;

public class SelfTestPageTest extends AbstractWicketTest<WicketApplication> {

    @Test
    public void shouldRenderSelfTestPage() {
        wicketTester.goTo(SelfTestPage.class)
                .should().containComponent(withId("serviceStatusTable"));
    }

}
