package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.sbl.dialogarena.mininnboks.consumer.TekstService;
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils;
import no.nav.sbl.tekster.TeksterAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import javax.inject.Inject;
import java.io.IOException;

@Configuration
public class TeksterServiceConfig {

    @Inject
    ResourceLoader loader;

    @Bean
    public TekstService tekstService() throws IOException {
        String ledeteksterPath = loader.getResource("classpath:tekster").getFile().getPath();
        TeksterAPI teksterAPI = new TeksterAPI(ledeteksterPath, "mininnboks");
        TekstService teksterService = new TekstService.Default(teksterAPI);
        HenvendelsesUtils.setTekstService(teksterService);
        return teksterService;
    }
}
