package no.nav.sbl.dialogarena.minehenvendelser.config;


import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import java.net.URL;

@Profile("test")
@Configuration
@Import(ConsumerTestContext.class)
@PropertySource("classpath:environment-test.properties")
public class TestApplicationContext implements SystemConfiguration {

    @Value("${henvendelser.ws.url}")
    private URL endpoint;

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingsServicePort();
    }

}
