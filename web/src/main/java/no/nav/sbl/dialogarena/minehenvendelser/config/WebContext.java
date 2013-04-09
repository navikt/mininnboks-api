package no.nav.sbl.dialogarena.minehenvendelser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebContext {

    @Bean
    public WicketApplication minehenvendelserApplication() {
        return new WicketApplication();
    }

}
