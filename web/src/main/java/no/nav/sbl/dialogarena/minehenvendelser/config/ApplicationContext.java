package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.sbl.dialogarena.minehenvendelser.WicketApplication;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationContext {

    @Bean
    public WicketApplication minehenvendelserApplication() {
        return new WicketApplication();
    }
    
    @Bean
    public BehandlingConsumer behandlingConsumer() {
        return new BehandlingConsumer();
    }

}
