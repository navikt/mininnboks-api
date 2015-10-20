package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.modig.lang.option.Optional.optional;

@Configuration
public class PersonServiceMockContext {

    @Bean
    public PersonService personService() {
        return new PersonService() {
            @Override
            public Optional<String> hentEnhet() {
                return optional("1234");
            }
        };
    }
}
