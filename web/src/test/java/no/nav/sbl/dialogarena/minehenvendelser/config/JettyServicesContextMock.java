package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.sbl.dialogarena.minehenvendelser.FoedselsnummerService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.HenvendelsesBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingskjedensBehandlingerResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.hentbehandlingskjedensbehandlinger.Behandlingskjede;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.config.JettyMockApplicationContext.AKTOR_ID;
import static no.nav.sbl.dialogarena.minehenvendelser.config.JettyMockApplicationContext.BEHANDLINGS_ID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
public class JettyServicesContextMock {

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer() {
        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource("environment-test.properties"));
        return placeholderConfigurer;
    }

    @Bean
    public MockData mockData() {
        MockData mockData = new MockData();
        mockData.getMockHentBehandlingskjedensBehandlingerData().addResponse(BEHANDLINGS_ID, new HentBehandlingskjedensBehandlingerResponse().withResponse(new no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse().withBehandlingskjede(createBehandlingsKjedeWithLinkedBehandling())));
        mockData.getFinnData().addResponse(AKTOR_ID, new FinnSakOgBehandlingskjedeListeResponse().withResponse(new no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse()));
        return mockData;
    }

    private Behandlingskjede createBehandlingsKjedeWithLinkedBehandling() {
        return null;
    }

    @Bean
    public SakogbehandlingService sakogbehandlingService() {
        return new SakogbehandlingService();
    }

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        return new SakOgBehandlingPortTypeMock();
    }

    @Bean
    public FoedselsnummerService foedselsnummerService() {
        return new FoedselsnummerService();
    }

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingsServicePort();
    }

    @Bean
    public HenvendelsesBehandlingPortType getHenvendelsesBehandlingPortType() {
        HenvendelsesBehandlingPortType henvendelsesBehandlingPortType = mock(HenvendelsesBehandlingPortType.class);
        when(henvendelsesBehandlingPortType.hentBrukerBehandlingListe(AKTOR_ID)).thenReturn(createListWithOneOfEachBehandling());
        return henvendelsesBehandlingPortType;
    }

    private List<WSBrukerBehandlingOppsummering> createListWithOneOfEachBehandling() {
        ArrayList<WSBrukerBehandlingOppsummering> wsBrukerBehandlingOppsummerings = new ArrayList<>();
        return wsBrukerBehandlingOppsummerings;
    }

    //Duplikat bønne for å få selftest til å kjøre med username-token (system-SAML). Skal fjernes når dette konfigureres gjennom wsdl
    @Bean(name = "selfTestHenvendelsesBehandlingPortType")
    public HenvendelsesBehandlingPortType selfTestHenvendelsesBehandlingPortType() {
        return new HenvendelsesBehandlingPortTypeMock();
    }

}
