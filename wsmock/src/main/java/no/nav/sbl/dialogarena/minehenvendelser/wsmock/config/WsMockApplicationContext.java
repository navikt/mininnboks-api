package no.nav.sbl.dialogarena.minehenvendelser.wsmock.config;

import javax.inject.Inject;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.HentBehandlingWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerTestContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

@Configuration
@Import({ConsumerTestContext.class})
public class WsMockApplicationContext {

    @Inject
    private MockData mockData;

    @Bean
    public WebServer mockSoapServer() {
        return WebServers.createWebServer(41001)
                .add("/wsmock/behandlinger", new HentBehandlingWebServiceMock(mockData));
    }
}
