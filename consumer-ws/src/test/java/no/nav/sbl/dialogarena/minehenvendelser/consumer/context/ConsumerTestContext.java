package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.BehandlingResponseMarshaller;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.HentBehandlingWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

import java.net.URL;
import java.util.concurrent.ExecutionException;

@Configuration
@Import(ConsumerContext.class)
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
    public BehandlingResponseMarshaller behandlingResponseMarshaller(){
        return new BehandlingResponseMarshaller(jaxb2Marshaller());
    }

    @Bean
    public MockData mockData() {
        MockData mockData = new MockData();
        return mockData;
    }


    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(
                no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.ObjectFactory.class,
                no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.ObjectFactory.class,
                no.nav.sbl.dialogarena.minehenvendelser.consumer.soap.ObjectFactory.class);
        return marshaller;
    }

    @Bean
    public WebServer webbitWebserver() throws ExecutionException, InterruptedException {
        WebServer server = WebServers.createWebServer(endpoint.getPort())
                .add(endpoint.getPath(), new HentBehandlingWebServiceMock(behandlingResponseMarshaller(), mockData()));
        server.start().get();
        return server;
    }

}
