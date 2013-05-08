package no.nav.sbl.dialogarena.minehenvendelser.config;

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
@Import(FooterConfig.class)
public class ApplicationContext {

    @Value("${minehenvendelser.navigasjonslink.url}")
    private String navigasjonslink;
    @Value("${dokumentinnsending.link.url}")
    private String dokumentInnsendingBaseUrl;

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

    @Bean
    public String dokumentInnsendingBaseUrl() {
        return dokumentInnsendingBaseUrl;
    }

}
