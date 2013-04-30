package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.wicket.test.FluentWicketTester;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

import javax.inject.Inject;
import java.util.Locale;

@Import(value = {ApplicationContext.class})
@PropertySource({"/environment-test.properties"})
public class FitNesseApplicationContext {

    @Inject
    private WicketApplication application;

    @Bean
    public FluentWicketTester<WicketApplication> wicketTester() {
        FluentWicketTester<WicketApplication> wicketTester = new FluentWicketTester<WicketApplication>(application);
        wicketTester.tester.getSession().setLocale(new Locale("nb"));
        return wicketTester;
    }

}
