package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseServiceMock;
import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.HentDiskresjonskodeRequest;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.HentDiskresjonskodeResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ApplicationContext.class)
public class MockApplicationContext {
    @Bean
    public HenvendelseService henvendelseService() {
        return new HenvendelseServiceMock();
    }
    @Bean
    public DiskresjonskodePortType diskresjonskodePortType() {
        return new DiskresjonskodePortType() {
            @Override
            public HentDiskresjonskodeResponse hentDiskresjonskode(HentDiskresjonskodeRequest request) {
                HentDiskresjonskodeResponse hentDiskresjonskodeResponse = new HentDiskresjonskodeResponse();
                hentDiskresjonskodeResponse.setDiskresjonskode("7");
                return hentDiskresjonskodeResponse;
            }
        };
    }
}
