package no.nav.sbl.dialogarena.minehenvendelser.config;

import javax.inject.Inject;

import no.nav.modig.wicket.test.FluentWicketTester;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FluentWicketTesterContext {
    
    @Inject
    private WicketApplication application;

    @Bean
    public FluentWicketTester<WicketApplication> fluentWicketTester() {
        return new FluentWicketTester<>(application);
    }
}
