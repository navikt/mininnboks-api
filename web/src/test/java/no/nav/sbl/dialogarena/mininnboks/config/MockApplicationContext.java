package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static no.nav.modig.lang.option.Optional.optional;

@Configuration
@Import({MinInnboksApplicationContext.class, HenvendelseMockContext.class})
public class MockApplicationContext {

    @Bean
    public PersonService personService() {
        return new PersonService() {
            @Override
            public String hentEpostadresse() throws Exception {
                String brukerId = SubjectHandler.getSubjectHandler().getUid();
                if (brukerId.startsWith("2")) {
                    return "epostadresse@example.com";
                } else if (brukerId.startsWith("9")) {
                    Exception e = new HentKontaktinformasjonOgPreferanserPersonIkkeFunnet("Dette er message fra HentKontaktinformasjonOgPreferanserPersonIkkeFunnet");
                    throw new Exception("Person med id '" + brukerId + "': " + e.getMessage(), e);
                } else {
                    return "";
                }
            }

            @Override
            public Optional<String> hentEnhet() {
                return optional("1234");
            }
        };
    }

}
