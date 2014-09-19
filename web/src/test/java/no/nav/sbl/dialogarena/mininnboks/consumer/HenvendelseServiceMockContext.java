package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.sbl.dialogarena.mininnboks.config.MockApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class HenvendelseServiceMockContext extends MockApplicationContext {

    @Override
    @Bean
    public HenvendelseService henvendelseService() {
        return mock(HenvendelseService.class);
    }

}
