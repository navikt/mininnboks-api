package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.sbl.dialogarena.mininnboks.consumer.TekstService;
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils;
import no.nav.sbl.tekster.TeksterAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;

@Configuration
public class TeksterServiceConfig {


    @Value("${folder.ledetekster.path}")
    private String ledeteksterPath;

    @Bean
    public TekstService tekstService() throws IOException {
        TeksterAPI teksterAPI = new TeksterAPI(ledeteksterPath + "/tekster", "mininnboks");
        TekstService teksterService = new TekstService.Default(teksterAPI);
        HenvendelsesUtils.setTekstService(teksterService);
        return teksterService;
    }
}
