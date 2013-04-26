package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import({ WebContext.class, ConsumerContext.class})
public class ApplicationContext {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

}
