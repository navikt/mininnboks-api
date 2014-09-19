package no.nav.sbl.dialogarena.mininnboks.consumer;

import no.nav.sbl.dialogarena.mininnboks.config.MockApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class EpostServiceMockContext extends MockApplicationContext {

    @Override
    @Bean
    public EpostService epostService() {
        return mock(EpostService.class);
    }
}
