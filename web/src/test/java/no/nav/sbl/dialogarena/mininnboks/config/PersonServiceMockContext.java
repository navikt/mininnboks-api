package no.nav.sbl.dialogarena.mininnboks.config;
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.Optional.of;

@Configuration
public class PersonServiceMockContext {

    @Bean
    public PersonService personService() {
        return () -> of("1234");
    }
}
