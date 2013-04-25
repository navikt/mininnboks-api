package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.wicket.test.FluentWicketTester;
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
