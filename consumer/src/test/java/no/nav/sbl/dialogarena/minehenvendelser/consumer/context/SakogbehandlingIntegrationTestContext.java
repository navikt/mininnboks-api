package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingWebServiceMock;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.webbitserver.WebServer;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static javax.xml.datatype.DatatypeFactory.newInstance;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.AKTOR_ID;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFinnSakOgBehandlingskjedeListeResponse;
import static org.webbitserver.WebServers.createWebServer;

@Configuration
public class SakogbehandlingIntegrationTestContext {

    @Value("${test.sakogbehandling.ws.url}")
    private URL endpoint;

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer() {
        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource("environment-test.properties"));
        return placeholderConfigurer;
    }

    @Bean
    public WebServer webbitWebserver() throws InterruptedException {
        try {
            return createWebServer(endpoint.getPort()).add(endpoint.getPath(), new SakogbehandlingWebServiceMock(mockData())).start().get();
        } catch (ExecutionException e) {
            throw new ApplicationException("Stopp Jetty!!!", e);
        }
    }

    @Bean
    public MockData mockData() {
        MockData mockData = new MockData();
        mockData.getFinnData().addResponse(AKTOR_ID, createFinnSakOgBehandlingskjedeListeResponse(populateFinnbehandlingKjedeList()));
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

    private static List<Behandlingskjede> populateFinnbehandlingKjedeList() {
        List<Behandlingskjede> behandlingsKjeder = new ArrayList<>();
        behandlingsKjeder.add(createFinnbehandlingKjede("Uførepensjon", "MOCK-00-00-00", true));
        behandlingsKjeder.add(createFinnbehandlingKjede("Sykepenger", "MOCK-10-00-00", true));
        behandlingsKjeder.add(createFinnbehandlingKjede("Arbeidsavklaringspenger", "MOCK-20-00-00", true));
        behandlingsKjeder.add(createFinnbehandlingKjede("Uførepensjon", "MOCK-30-00-00", false));
        behandlingsKjeder.add(createFinnbehandlingKjede("Sykepenger", "MOCK-44-00-00", false));
        return behandlingsKjeder;
    }

    private static Behandlingskjede createFinnbehandlingKjede(String value, String kodeverkRef, boolean isFerdig) {
        Behandlingskjede behandlingskjede = new Behandlingskjede()
                .withStartNAVtid(createXmlGregorianDate(1, 1, 2013))
                .withNormertBehandlingstid(new Behandlingstid().withTid(BigInteger.TEN).withType(new Behandlingstidtyper()))
                .withBehandlingskjedetype(new Behandlingskjedetyper().withValue(value).withKodeverksRef(kodeverkRef));

        if(isFerdig) {
            behandlingskjede.setSluttNAVtid(createXmlGregorianDate(1, 1, 2014));
        }

        return behandlingskjede;
    }

    private static XMLGregorianCalendar createXmlGregorianDate(int day, int month, int year) {
        DateTime dateTime = new DateTime().withDate(year, month, day);
        XMLGregorianCalendar xmlGregorianCalendar;
        try {
            xmlGregorianCalendar = newInstance().newXMLGregorianCalendar(dateTime.toGregorianCalendar());
        } catch (DatatypeConfigurationException e) {
            throw new ApplicationException("Failed to convert date to XMLGregorianCalendar ",e);
        }
        return xmlGregorianCalendar;
    }
}
