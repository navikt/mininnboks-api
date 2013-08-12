package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.integration.HentBehandlingWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.meldinger.HentBrukerBehandlingListeResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.webbitserver.WebServer;

import java.net.URL;
import java.util.concurrent.ExecutionException;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFerdigBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFerdigBehandlingMedAlleInnsendt;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFerdigBehandlingMedIngenInnsendt;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFerdigEttersendingBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFitnesseTestData;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createUnderArbeidBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createUnderArbeidEttersendingBehandling;
import static org.webbitserver.WebServers.createWebServer;

@Configuration
public class HenvendelseConsumerTestContext {

    @Value("${test.henvendelser.ws.url}")
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
        mockData.getHentData().addResponse("***REMOVED***", new HentBrukerBehandlingListeResponse().withBrukerBehandlinger(createFerdigBehandling(), createFerdigEttersendingBehandling(), createUnderArbeidBehandling(), createUnderArbeidEttersendingBehandling()));
        mockData.getHentData().addResponse("***REMOVED***", new HentBrukerBehandlingListeResponse().withBrukerBehandlinger(createFerdigBehandlingMedAlleInnsendt(), createFerdigBehandlingMedIngenInnsendt()));
        mockData.getHentData().addResponse("test", new HentBrukerBehandlingListeResponse().withBrukerBehandlinger(createFitnesseTestData()));
        return mockData;
    }

    @Bean
    public WebServer webbitWebserver() throws InterruptedException {
        try {
            return createWebServer(endpoint.getPort()).add(endpoint.getPath(), new HentBehandlingWebServiceMock(mockData())).start().get();
        } catch (ExecutionException e) {
            throw new ApplicationException("Stopp Jetty!!!", e);
        }
    }

    @Bean
    public HenvendelsesBehandlingPortType getHenvendelsesBehandlingPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceClass(HenvendelsesBehandlingPortType.class);
        proxyFactoryBean.setAddress(endpoint.toString());
        return proxyFactoryBean.create(HenvendelsesBehandlingPortType.class);
    }

}
