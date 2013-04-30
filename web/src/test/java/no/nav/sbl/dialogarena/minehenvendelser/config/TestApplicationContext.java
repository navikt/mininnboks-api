package no.nav.sbl.dialogarena.minehenvendelser.config;


import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdDummy;
import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdService;
import no.nav.sbl.dialogarena.minehenvendelser.KodeverkServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile("test")
@Configuration
@Import(ConsumerTestContext.class)
@PropertySource("classpath:environment-test.properties")
public class TestApplicationContext {

    @Bean
    public KodeverkService kodeverkOppslag() {
        return new KodeverkServiceMock();
    }

    @Bean
    public AktoerIdService aktoerIdService() {
        return new AktoerIdDummy();
    }

}
