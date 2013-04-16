package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.BehandlingerResponse;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.config.WsMockApplicationContext;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.consumer.context.WsMockTestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.inject.Inject;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

@Configuration
@Import({ WebContext.class, PropertyPlaceholderConfiguration.class, LedeteksterConfiguration.class })
public class ApplicationContext {

    @Configuration
    @Profile("test")
    @Import({ ConsumerContext.class, WsMockTestContext.class })
    public static class ServiceTestContext {
    }

    @Configuration
    @Profile("default")
    @Import(ConsumerContext.class)
    public static class ServiceRealContext {
    }

    @Configuration
    @Profile("stub")
    @Import({ ConsumerContext.class, WsMockApplicationContext.class })
    public static class ServiceStubContext {

        @Inject
        private Jaxb2Marshaller jaxb2Marshaller;

        @Bean
        public MockData mockData() {
            MockData mockData = new MockData();
            mockData.clearResponse();
            InputStream inputStream = getClass().getResourceAsStream("/mockdata/behandlinger.xml");
            BehandlingerResponse behandlingerResponse = (BehandlingerResponse)jaxb2Marshaller.unmarshal(new StreamSource(inputStream));
            mockData.addBehandlingerToResponse(behandlingerResponse.getBehandlinger());
            return mockData;
        }

    }

}
