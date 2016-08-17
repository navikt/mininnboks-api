package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.modig.cache.CacheConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import({ContentConfig.class, PersonServiceMockContext.class, HenvendelseMockContext.class, CacheConfig.class})
public class MockApplicationContext {
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
