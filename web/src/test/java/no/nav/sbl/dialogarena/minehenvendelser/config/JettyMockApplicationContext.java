package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.cache.CacheConfig;
import no.nav.sbl.dialogarena.common.kodeverk.config.KodeverkConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

/**
 * Produksjonskontekst
 */
@Configuration
@PropertySource("environment.properties") // fjern denne a plis
@Import({
        CacheConfig.class,
        JaxWsFeatures.Mock.class,
        WebContext.class,
        KodeverkConfig.class,
        JettyServicesContextMock.class
})
public class JettyMockApplicationContext {

    public static final String AKTOR_ID = "***REMOVED***";
    public static final String BEHANDLINGS_ID = "behId01";

}
