package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.sbl.dialogarena.mininnboks.consumer.TekstService;
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstServiceImpl;
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TeksterServiceConfig {

    @Bean
    public TekstService tekstService() {
        TekstService teksterService = new TekstServiceImpl();
        HenvendelsesUtils.setTekstService(teksterService);
        return teksterService;
    }

}
