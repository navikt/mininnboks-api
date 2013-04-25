package no.nav.sbl.dialogarena.minehenvendelser.wsmock.config;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerContext;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.HentBehandlingerWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.MockData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.webbitserver.WebServer;
    import org.webbitserver.WebServers;

import javax.inject.Inject;

@Configuration
@Import({ ConsumerContext.class })
public class WsMockApplicationContext {

    private static final Logger logger = LoggerFactory.getLogger(WsMockApplicationContext.class);

    @Bean
    public MockData mockData() {
        logger.info(":::: Entered mockData!");
        MockData mockData = new MockData();
        return mockData;
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
