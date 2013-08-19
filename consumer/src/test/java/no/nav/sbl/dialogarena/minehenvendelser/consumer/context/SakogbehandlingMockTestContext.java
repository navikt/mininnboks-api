package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import static org.mockito.Mockito.mock;

@Configuration
public class SakogbehandlingMockTestContext {

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer() {
        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource("environment-test.properties"));
        return placeholderConfigurer;
    }

    @Bean
    public MockData mockData() {
        return new MockData();
    }

    @Bean
    public SakogbehandlingService sakogbehandlingService() {
        return new SakogbehandlingService();
    }

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        return new SakOgBehandlingPortTypeMock();
    }

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingsServicePort();
    }

    @Bean
    public HenvendelsesBehandlingPortType getHenvendelsesBehandlingPortType() {
        return mock(HenvendelsesBehandlingPortType.class);
    }

}
