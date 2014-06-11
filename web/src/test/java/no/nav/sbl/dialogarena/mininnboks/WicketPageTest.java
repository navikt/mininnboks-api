package no.nav.sbl.dialogarena.mininnboks;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.mininnboks.config.PageTestContext;
import no.nav.sbl.dialogarena.test.SystemProperties;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PageTestContext.class})
public abstract class WicketPageTest {

    @Inject
    protected FluentWicketTester<?> wicketTester;

    @BeforeClass
    public static void setupStatic() {
        SystemProperties.setFrom("jetty-mininnboks.properties");
    }
}
