package no.nav.sbl.dialogarena.minehenvendelser.wsmock.config;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.BehandlingResponseMarshaller;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.HentBehandlingWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerTestContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import javax.inject.Inject;

@Configuration
@Import({ConsumerTestContext.class})
public class WsMockApplicationContext {

    @Inject
    private MockData mockData;
    @Inject
    private BehandlingResponseMarshaller marshaller;

    @Bean
    public WebServer mockSoapServer() {
        return WebServers.createWebServer(41001)
                .add("/wsmock/behandlinger", new HentBehandlingWebServiceMock(marshaller, mockData));
    }
}
