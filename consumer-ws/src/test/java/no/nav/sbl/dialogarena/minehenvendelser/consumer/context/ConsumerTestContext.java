package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.HentBehandlingWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

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
    public MockData mockData() {
        MockData mockData = new MockData();
        return mockData;
    }

    @Bean
    public WebServer webbitWebserver() throws ExecutionException, InterruptedException {
        WebServer server = WebServers.createWebServer(endpoint.getPort())
                .add(endpoint.getPath(), new HentBehandlingWebServiceMock(mockData()));
        server.start().get();
        return server;
    }

}
