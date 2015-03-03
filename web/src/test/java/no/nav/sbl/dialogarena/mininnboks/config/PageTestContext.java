package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.modig.content.PropertyResolver;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.mininnboks.WicketApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.inject.Inject;

import static org.mockito.Mockito.mock;

@Configuration
@Import(MockApplicationContext.class)
public class PageTestContext {

    @Inject
    private WicketApplication application;

    @Bean
    public FluentWicketTester<WicketApplication> fluentWicketTester() {
        return new FluentWicketTester<>(application);
    }

    @Bean
    public PropertyResolver propertyResolver() {
        return mock(PropertyResolver.class);
    }
}
