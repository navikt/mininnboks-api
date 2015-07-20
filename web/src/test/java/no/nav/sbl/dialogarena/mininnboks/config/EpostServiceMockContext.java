package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
public class EpostServiceMockContext extends MockApplicationContext {

    @Override
    @Bean
    public PersonService personService() {
        return mock(PersonService.class);
    }
}
