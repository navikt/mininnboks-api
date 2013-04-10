package no.nav.sbl.dialogarena.minehenvendelser.selftest;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.minehenvendelser.config.WicketApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Locale;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WicketApplication.class)
@ActiveProfiles("test")
public class SelfTestPageTest {

    @Inject
    private WicketApplication minehenvendelserApplication;

    @Test
    public void shouldRenderSelfTestPage() {
        FluentWicketTester<WicketApplication> wicketTester = new FluentWicketTester<>(minehenvendelserApplication);
        wicketTester.tester.getSession().setLocale(new Locale("NO"));

        wicketTester.goTo(SelfTestPage.class)
                .should().containComponent(withId("serviceStatusTable"));
    }

}
