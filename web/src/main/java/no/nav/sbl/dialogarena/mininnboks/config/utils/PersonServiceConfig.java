package no.nav.sbl.dialogarena.mininnboks.config.utils;

import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import no.nav.tjeneste.virksomhet.brukerprofil.v2.BrukerprofilV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils.createPortType;

@Configuration
public class PersonServiceConfig {
    @Bean
    public PersonService personService() {
        return new PersonService.Default(createBrukerprofilV2());
    }


    private BrukerprofilV2 createBrukerprofilV2() {
        return createPortType(System.getProperty("brukerprofil.v2.url"),
                "",
                BrukerprofilV2.class,
                true);
    }
}
