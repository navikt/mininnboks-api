package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.minehenvendelser.config.WicketApplicationContext;
import org.apache.wicket.protocol.http.WebApplication;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Locale;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WicketApplicationContext.class)
@ActiveProfiles("test")
public abstract class AbstractWicketTest<T extends WebApplication> {

    @Inject
    protected FluentWicketTester<T> wicketTester;

    @Before
    public void before() {
        wicketTester.tester.getSession().setLocale(new Locale("NO"));
    }

}
