package no.nav.sbl.dialogarena.minehenvendelser.wsmock.config;

import no.nav.sbl.dialogarena.minehenvendelser.wsmock.HentBehandlingerWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.MockData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import javax.inject.Inject;

@Configuration
public class WsMockApplicationContext {

    @Bean
    public MockData mockData() {
        return new MockData();
    }

    @Configuration
    public static class MockServerConfig {

        @Inject
        private MockData mockData;

        @Bean
        public WebServer mockSoapServer() {
            return WebServers.createWebServer(41001)
                    .add("/wsmock/behandlinger", new HentBehandlingerWebServiceMock(mockData));
        }


    }

}
