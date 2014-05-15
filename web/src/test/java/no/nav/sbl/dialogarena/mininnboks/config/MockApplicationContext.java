package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseServiceMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ApplicationContext.class)
public class MockApplicationContext {
    @Bean
    public HenvendelseService henvendelseService() {
        return new HenvendelseServiceMock();
    }
}
