package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.cache.CacheConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Hovedkontekstfil for applikasjonen. Laster inn subkontekster for både default- og testprofil.
 */
@ComponentScan("no.nav.sbl.dialogarena.minehenvendelser.config")
@Configuration
@Import({CacheConfig.class})
public class ApplicationContext {

    @Value("${minehenvendelser.navigasjonslink.url}")
    private String navigasjonslink;

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public WicketApplication minehenvendelserApplication() {
        return new WicketApplication();
    }

    @Bean
    public String navigasjonslink() {
        return navigasjonslink;
    }

}
