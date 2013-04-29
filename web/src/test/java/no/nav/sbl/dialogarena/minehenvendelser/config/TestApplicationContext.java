package no.nav.sbl.dialogarena.minehenvendelser.config;


import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import org.springframework.context.annotation.*;

@Profile("test")
@Configuration
@Import(ConsumerTestContext.class)
@PropertySource("classpath:environment-test.properties")
public class TestApplicationContext implements SystemConfiguration {

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingsServicePort();
    }

}
