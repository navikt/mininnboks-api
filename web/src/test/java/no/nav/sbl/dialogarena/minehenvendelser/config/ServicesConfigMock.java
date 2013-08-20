package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.sbl.dialogarena.minehenvendelser.FoedselsnummerService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.HenvendelsesBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfigMock  {

    @Bean
    public SakogbehandlingService sakogbehandlingService() {
        return new SakogbehandlingService();
    }

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        return new SakOgBehandlingPortTypeMock();
    }

    @Bean
    public SakOgBehandlingPortType selfTestSakOgBehandlingPortType() {
        return new SakOgBehandlingPortTypeMock();
    }

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingsServicePort();
    }

    @Bean
    public FoedselsnummerService foedselsnummerService() {
        return new FoedselsnummerService();
    }

    @Bean
    public HenvendelsesBehandlingPortType getHenvendelsesBehandlingPortType() {
        return new HenvendelsesBehandlingPortTypeMock();
    }

    //Duplikat bønne for å få selftest til å kjøre med username-token (system-SAML). Skal fjernes når dette konfigureres gjennom wsdl
    @Bean(name = "selfTestHenvendelsesBehandlingPortType")
    public HenvendelsesBehandlingPortType selfTestHenvendelsesBehandlingPortType() {
        return new HenvendelsesBehandlingPortTypeMock();
    }
}
