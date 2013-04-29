package no.nav.sbl.dialogarena.minehenvendelser.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.net.URL;

@ComponentScan("no.nav.sbl.dialogarena.minehenvendelser.config")
@Configuration
public class ApplicationContext {

    @Value("${henvendelser.ws.url}")
    private URL endpoint;

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public WicketApplication minehenvendelserApplication() {
        return new WicketApplication();
    }

}
