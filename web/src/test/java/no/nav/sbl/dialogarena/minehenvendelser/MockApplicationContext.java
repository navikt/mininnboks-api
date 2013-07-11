package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.cache.CacheConfig;
import no.nav.sbl.dialogarena.common.kodeverk.config.KodeverkConfig;
import no.nav.sbl.dialogarena.minehenvendelser.config.JaxWsFeatures;
import no.nav.sbl.dialogarena.minehenvendelser.config.ServicesConfig;
import no.nav.sbl.dialogarena.minehenvendelser.config.WebContext;
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
        ServicesConfig.class,
        WebContext.class,
        KodeverkConfig.class
})
public class MockApplicationContext {
}
