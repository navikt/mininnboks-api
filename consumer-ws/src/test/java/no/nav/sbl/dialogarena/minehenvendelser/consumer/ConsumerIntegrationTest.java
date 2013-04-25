package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import javax.inject.Inject;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConsumerTestContext.class})
public class ConsumerIntegrationTest {

    private WebServer server;
    @Inject
    private BehandlingService service;
    @Value("${henvendelser.ws.url}")
    private URL endpoint;
    @Inject
    private BehandlingResponseMarshaller marshaller;

    @Test
    public void shouldIntegrateWithHenvendelserViaWebService() {
        List<Behandling> behandlingList = service.hentBehandlinger("test");
        assertNotNull(behandlingList);
        assertThat(behandlingList.size(), equalTo(1));
    }

    @Before
    public void startWebbit() throws ExecutionException, InterruptedException {
        server = WebServers.createWebServer(endpoint.getPort())
                .add(endpoint.getPath(), new HentBehandlingWebServiceMock(marshaller));
        server.start().get();
    }

    @After
    public void stopWebbit() {
        server.stop();
    }
}
