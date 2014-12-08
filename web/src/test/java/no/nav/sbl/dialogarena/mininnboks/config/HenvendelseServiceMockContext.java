package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.mockito.Mockito.mock;

@Configuration
@Import(MockApplicationContext.class)
public class HenvendelseServiceMockContext {
    @Bean
    public HenvendelseService henvendelseService() {
        return mock(HenvendelseService.class);
    }

}
