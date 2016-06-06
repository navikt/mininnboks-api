package no.nav.sbl.dialogarena.mininnboks.config.utils;

import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils.createPortType;

@Configuration
public class PersonServiceConfig {
    @Bean
    public PersonService personService() {
        return new PersonService.Default(createBrukerprofilV3());
    }


    private BrukerprofilV3 createBrukerprofilV3() {
        return createPortType(System.getProperty("brukerprofil.v3.url"),
                "",
                BrukerprofilV3.class,
                true);
    }
}
