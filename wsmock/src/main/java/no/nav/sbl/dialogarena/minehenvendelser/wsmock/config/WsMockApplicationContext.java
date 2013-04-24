package no.nav.sbl.dialogarena.minehenvendelser.wsmock.config;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingerResponse;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingerResponse;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.HentBehandlingerWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.MockData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import javax.inject.Inject;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

@Configuration
@Import({ ConsumerContext.class })
public class WsMockApplicationContext {

    private static final Logger logger = LoggerFactory.getLogger(WsMockApplicationContext.class);

    @Inject
    private Jaxb2Marshaller jaxb2Marshaller;

    @Bean
    public MockData mockData() {
        logger.info(":::: Entered mockData!");
        MockData mockData = new MockData();
        mockData.clearResponse();
        logger.info(":::: Response cleared");
        InputStream inputStream = getClass().getResourceAsStream("/mockdata/behandlinger___.xml");
        BehandlingerResponse behandlingerResponse = (BehandlingerResponse)jaxb2Marshaller.unmarshal(new StreamSource(inputStream));
        logger.info(":::: BehandlingerResponse retrieved! Amount: " + behandlingerResponse.getBehandlinger().size());
        mockData.addBehandlingerToResponse(behandlingerResponse.getBehandlinger());
        logger.info(":::: Behandlinger added. Amount: " + mockData.getBehandlingerResponse().getBehandlinger().size());
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
