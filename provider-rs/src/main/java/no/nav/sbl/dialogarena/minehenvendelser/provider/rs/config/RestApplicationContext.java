package no.nav.sbl.dialogarena.minehenvendelser.provider.rs.config;

import no.nav.sbl.dialogarena.common.kodeverk.config.KodeverkConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        RestServiceConfig.class,
        KodeverkConfig.class,
        CmsConfig.class,
        JaxWsFeatures.DisableCNCheck.class
})
public class RestApplicationContext {
}
