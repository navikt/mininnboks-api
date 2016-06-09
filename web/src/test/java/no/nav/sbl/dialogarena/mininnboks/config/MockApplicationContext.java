package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.modig.cache.CacheConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({PersonServiceMockContext.class, HenvendelseMockContext.class, ContentConfig.class, CacheConfig.class})
public class MockApplicationContext {
}
