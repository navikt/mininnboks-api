package no.nav.sbl.dialogarena.minehenvendelser.test;

import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.minehenvendelser.config.WicketApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class FluentWicketTesterContext {
    
    @Inject
    private WicketApplication application;

    @Bean
    public FluentWicketTester<WicketApplication> fluentWicketTester() {
        return new FluentWicketTester<>(application);
    }
}
