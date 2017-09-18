package no.nav.sbl.dialogarena.mininnboks.config.utils;

import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import no.nav.tjeneste.virksomhet.organisasjonenhet.v2.OrganisasjonEnhetV2;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils.createPortType;

@Configuration
public class PersonServiceConfig {
    @Bean
    public PersonService personService() {
        return new PersonService.Default(createPersonV3(), createOrganisasjonEnhetV2());
    }


    private BrukerprofilV3 createBrukerprofilV3() {
        return createPortType(System.getProperty("brukerprofil.v3.url"),
                "",
                BrukerprofilV3.class,
                true);
    }

    private PersonV3 createPersonV3() {
        return createPortType(System.getProperty("person.v3.url"),
                "",
                PersonV3.class,
                true);
    }

    private OrganisasjonEnhetV2 createOrganisasjonEnhetV2() {
        return createPortType(System.getProperty("organisasjonenhet.v2.url"),
                "",
                OrganisasjonEnhetV2.class,
                true);
    }
}
