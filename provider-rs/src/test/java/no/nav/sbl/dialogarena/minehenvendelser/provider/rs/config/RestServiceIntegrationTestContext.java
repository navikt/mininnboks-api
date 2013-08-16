package no.nav.sbl.dialogarena.minehenvendelser.provider.rs.config;

import no.nav.sbl.dialogarena.common.kodeverk.config.KodeverkConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import java.net.URL;

@Configuration
@Import({
        KodeverkConfig.class
})
public class RestServiceIntegrationTestContext {

    @Value("${test.minehenvendelser.rest.url}")
    private URL endpoint;

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer() {
        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource("environment-test.properties"));
        return placeholderConfigurer;
    }

//    @Bean
//    public MockData mockData() {
//        MockData mockData = new MockData();
////        mockData.getHentData().addResponse("***REMOVED***", new HentBrukerBehandlingListeResponse().withBrukerBehandlinger(createUnderArbeidBehandling(), createUnderArbeidEttersendingBehandling()));
////        mockData.getHentData().addResponse("test", new HentBrukerBehandlingListeResponse().withBrukerBehandlinger(createFitnesseTestData()));
//        return mockData;
//    }

//    @Bean
//    public WebServer webbitWebserver() throws InterruptedException {
//        try {
//            return createWebServer(endpoint.getPort()).add(endpoint.getPath(), new HentBehandlingWebServiceMock(mockData())).start().get();
//        } catch (ExecutionException e) {
//            throw new ApplicationException("Stopp Jetty!!!", e);
//        }
//    }

}
