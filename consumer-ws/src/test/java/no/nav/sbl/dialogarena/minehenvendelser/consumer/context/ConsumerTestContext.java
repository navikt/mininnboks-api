package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.HentBehandlingWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HentBrukerBehandlingerResponse;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFerdigBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFerdigEttersendingBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFitnesseTestData;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createUnderArbeidBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createUnderArbeidEttersendingBehandling;

@Configuration
public class ConsumerTestContext {

    @Value("${henvendelser.ws.url}")
    private URL endpoint;

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer() {
        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource("environment-test.properties"));
        return placeholderConfigurer;
    }

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingsServicePort();
    }

    @Bean
    public MockData mockData() {
        MockData mockData = new MockData();
        mockData.addResponse("***REMOVED***", new HentBrukerBehandlingerResponse().withBrukerBehandlinger(createFerdigBehandling(), createFerdigEttersendingBehandling(), createUnderArbeidBehandling(), createUnderArbeidEttersendingBehandling()));
        mockData.addResponse("test", new HentBrukerBehandlingerResponse().withBrukerBehandlinger(createFitnesseTestData()));
        return mockData;
    }

    @Bean
    public WebServer webbitWebserver() throws InterruptedException {
        WebServer server = WebServers.createWebServer(endpoint.getPort())
                .add(endpoint.getPath(), new HentBehandlingWebServiceMock(mockData()));
        try {
            server.start().get();
        } catch (ExecutionException e) {
            throw new ApplicationException("Stopp Jetty!!!");
        }
        return server;
    }

    @Bean
    public HenvendelsesBehandlingPortType jaxWsClientFactoryBean() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceClass(HenvendelsesBehandlingPortType.class);
        proxyFactoryBean.setAddress(endpoint.toString());
        return proxyFactoryBean.create(HenvendelsesBehandlingPortType.class);
    }

}
