package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.modig.cache.CacheConfig;
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import no.nav.tjeneste.virksomhet.person.v2.PersonV2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils.createPortType;

@Configuration
@Import({CacheConfig.class, ContentConfig.class, HenvendelseServiceConfig.class, Pingables.class})
public class MinInnboksApplicationContext {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PersonService personService() {
        return new PersonService.Default(brukerprofilSSO(), personV2());
    }



    private BrukerprofilPortType brukerprofilSSO() {
        return createPortType(System.getProperty("brukerprofil.ws.url"),
                "classpath:brukerprofil/no/nav/tjeneste/virksomhet/brukerprofil/v1/Brukerprofil.wsdl",
                BrukerprofilPortType.class,
                true);
    }

    private PersonV2 personV2() {
        return createPortType(System.getProperty("brukerprofil.ws.url"),
                "classpath:no/nav/tjeneste/virksomhet/person/v2/person.wsdl",
                PersonV2.class,
                true);
    }
}
