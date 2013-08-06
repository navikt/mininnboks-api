package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.HentSakogbehandlingWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Temaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.webbitserver.WebServer;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import static org.webbitserver.WebServers.createWebServer;

@Configuration
public class SakogbehandlingTestContext {

    @Value("${sakogbehandling.ws.url}")
    private URL endpoint;

    @SuppressWarnings({"PMD.PreserveStackTrace"})
    @Bean
    public WebServer webbitWebserver() throws InterruptedException {
        WebServer server = createWebServer(endpoint.getPort()).add(endpoint.getPath(), new HentSakogbehandlingWebServiceMock(mockData()));
        try {
            server.start().get();
        } catch (ExecutionException e) {
            throw new ApplicationException("Stopp Jetty!!!");
        }
        return server;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource("environment-test.properties"));
        return placeholderConfigurer;
    }

    @Bean
    public MockData mockData() {
        MockData mockData = new MockData();
        mockData.addResponse("***REMOVED***", new FinnSakOgBehandlingskjedeListeResponse().withResponse(new no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse().withSak(new Sak().withTema(new Temaer().withKodeverksRef("Tema")))));
        mockData.addResponse("***REMOVED***", new FinnSakOgBehandlingskjedeListeResponse());
        mockData.addResponse("test", new FinnSakOgBehandlingskjedeListeResponse());
        return mockData;
    }

    @Bean
    public SakogbehandlingService sakogbehandlingService() {
        return new SakogbehandlingService();
    }

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceClass(SakOgBehandlingPortType.class);
        proxyFactoryBean.setAddress(endpoint.toString());
        return proxyFactoryBean.create(SakOgBehandlingPortType.class);
    }
}
