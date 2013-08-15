package no.nav.sbl.dialogarena.minehenvendelser.provider.rs.config;

import no.nav.sbl.dialogarena.minehenvendelser.provider.rs.InnsendingerProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestServiceConfig {

    @Bean
    public InnsendingerProvider innsendingerProvider() {
        return new InnsendingerProvider();
    }
}
