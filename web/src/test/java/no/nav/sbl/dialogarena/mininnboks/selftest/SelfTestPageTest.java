package no.nav.sbl.dialogarena.mininnboks.selftest;

import no.nav.sbl.dialogarena.mininnboks.WicketPageTest;
import org.junit.Test;

public class SelfTestPageTest extends WicketPageTest {

    @Test
    public void testSelfTestPage() {
        wicketTester.goTo(SelfTestPage.class).should().beOn(SelfTestPage.class);
    }
}
