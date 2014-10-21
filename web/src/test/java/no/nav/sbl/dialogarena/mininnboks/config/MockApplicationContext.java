package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.sbl.dialogarena.mininnboks.consumer.DiskresjonskodeService;
import no.nav.sbl.dialogarena.mininnboks.consumer.EpostService;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseServiceMock;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.HentKontaktinformasjonOgPreferanserPersonIkkeFunnet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MinInnboksApplicationContext.class)
public class MockApplicationContext {
    @Bean
    public HenvendelseService henvendelseService() {
        return new HenvendelseServiceMock();
    }

    @Bean
    public DiskresjonskodeService diskresjonskodeService() {
        return new DiskresjonskodeService() {
            @Override
            public String getDiskresjonskode(String fnr) {
                return "7";
            }
        };
    }

    @Bean
    public EpostService epostService() {
        return new EpostService() {
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
        };
    }

}
