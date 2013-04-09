package no.nav.sbl.dialogarena.minehenvendelser.wsmock.consumer.context;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.consumer.BehandlingServiceFilebased;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WsMockTestContext {

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingServiceFilebased();
    }

}
