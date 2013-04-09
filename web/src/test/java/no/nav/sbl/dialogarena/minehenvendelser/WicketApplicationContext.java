package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.minehenvendelser.config.ApplicationContext;
import no.nav.sbl.dialogarena.minehenvendelser.config.WicketApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;
import java.util.Locale;

@Import(ApplicationContext.class)
public class WicketApplicationContext {

    @Inject
    private WicketApplication application;

    @Bean
    public FluentWicketTester<WicketApplication> wicketTester() {
        FluentWicketTester<WicketApplication> wicketTester = new FluentWicketTester<WicketApplication>(application);
        wicketTester.tester.getSession().setLocale(new Locale("NO"));
        return wicketTester;
    }

}
