package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import javax.inject.Inject;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import java.net.URL;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConsumerTestContext.class})
public class ConsumerIntegrationTest {

    private WebServer server;

    @Inject
    private BehandlingService service;

    @Value("henvendelser.ws.url")
    private URL endpoint;

    @Test
    @Ignore
    public void shouldIntegrateWithHenvendelserViaWebService() {
        service.hentBehandlinger("test");
    }


    @Before
    public void startWebbit(){

        server = WebServers.createWebServer(41001)
                .add(endpoint.getPath(), new HttpHandler() {
                    @Override
                    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {


                    }
                });

    }

    @After
    public void stopWebbit(){
        server.stop();
    }


}
