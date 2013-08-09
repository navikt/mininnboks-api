package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.HentSakogbehandlingWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingsstegtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Temaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.webbitserver.WebServer;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.AKOTR_ID;
import static org.webbitserver.WebServers.createWebServer;

@Configuration
public class SakogbehandlingTestContext {

    @Value("${sakogbehandling.ws.url}")
    private URL endpoint;

    @SuppressWarnings({"PMD.PreserveStackTrace"})
    @Bean
    public WebServer webbitWebserver() throws InterruptedException {
        try {
            return createWebServer(endpoint.getPort()).add(endpoint.getPath(), new HentSakogbehandlingWebServiceMock(mockData())).start().get();
        } catch (ExecutionException e) {
            throw new ApplicationException("Stopp Jetty!!!");
        }
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
        mockData.getFinnData().addResponse("***REMOVED***",
                new FinnSakOgBehandlingskjedeListeResponse().
                        withResponse(new no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse().
                                withSak(new Sak().withTema(new Temaer().withKodeverksRef("Tema")).
                                        withBehandlingskjede(createDummyBehandlingkjede()))));
        mockData.getFinnData().addResponse("***REMOVED***", new FinnSakOgBehandlingskjedeListeResponse());
        mockData.getFinnData().addResponse("test", new FinnSakOgBehandlingskjedeListeResponse());
        mockData.getFinnData().addResponse(AKOTR_ID, MockCreationUtil.createSakOgBehandlingskjedeListeResponse());
        return mockData;
    }

    private Behandlingskjede createDummyBehandlingkjede() {
        return new Behandlingskjede()
                .withNormertBehandlingstid(new Behandlingstid().withType(new Behandlingstidtyper()))
                .withStartNAVtid(createDummyXMLGregorianCalendarDate())
                .withBehandlingskjedetype(new Behandlingskjedetyper())
                .withBehandlingskjedeId("id")
                .withKjedensNAVfrist(createDummyXMLGregorianCalendarDate())
                .withSisteBehandlingREF("sisteBehandlingref")
                .withSisteBehandlingsstegREF("sisteBehandlingsstegref")
                .withSisteBehandlingsstegtype(new Behandlingsstegtyper().withValue("value").withKodeRef("koderef").withKodeverksRef("kodeverksref"));
    }

    private XMLGregorianCalendar createDummyXMLGregorianCalendarDate() {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar());
        } catch (DatatypeConfigurationException e) {
            throw new SystemException("failed to create xmlgregcal instance", e);
        }
    }

    @Bean
    public SakogbehandlingService sakogbehandlingService() {
        return new SakogbehandlingService();
    }

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        return new SakOgBehandlingPortTypeMock();
    }
}
