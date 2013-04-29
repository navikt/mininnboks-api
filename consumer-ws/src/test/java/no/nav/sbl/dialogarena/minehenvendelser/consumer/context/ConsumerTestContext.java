package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.HentBehandlingWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import javax.xml.namespace.QName;
import java.net.URL;
import java.util.concurrent.ExecutionException;

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

//    @Bean
//    public BehandlingService behandlingService() {
//        return new BehandlingsServicePort();
//    }

    @Bean
    public MockData mockData() {
        return new MockData();
    }

    @Bean
    public WebServer webbitWebserver() throws ExecutionException, InterruptedException {
        WebServer server = WebServers.createWebServer(endpoint.getPort())
                .add(endpoint.getPath(), new HentBehandlingWebServiceMock(mockData()));
        server.start().get();
        return server;
    }

    @Bean
    public HenvendelsesBehandlingPortType jaxWsClientFactoryBean() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceName(new QName(endpoint.getPath()));
        proxyFactoryBean.setEndpointName(new QName(endpoint.getPath()));
        proxyFactoryBean.setAddress(endpoint.toString());
        return proxyFactoryBean.create(HenvendelsesBehandlingPortType.class);
    }

}
