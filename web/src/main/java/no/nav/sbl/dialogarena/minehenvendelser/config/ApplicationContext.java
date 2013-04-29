package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.inject.Inject;
import java.net.URL;

@ComponentScan("no.nav.sbl.dialogarena.minehenvendelser.config")
@Configuration
public class ApplicationContext {

    @Value("${henvendelser.ws.url}")
    private URL endpoint;

    @Inject
    private SystemConfiguration systemConfiguration;

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }


    @Bean
    public WicketApplication minehenvendelserApplication() {
        System.out.println(endpoint);
        return new WicketApplication();
    }

}
