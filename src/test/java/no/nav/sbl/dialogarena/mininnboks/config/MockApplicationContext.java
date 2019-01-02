package no.nav.sbl.dialogarena.mininnboks.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import({
        TeksterServiceConfig.class,
        PersonServiceMockContext.class,
        HenvendelseMockContext.class
})
public class MockApplicationContext {
    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
