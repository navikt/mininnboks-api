package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.HenvendelsesBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakOgBehandlingPortTypeMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfigMock  {

    @Bean
    public MeldingService meldingService() {
        return new MeldingService();
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
    public SakOgBehandlingPortType selfTestSakOgBehandlingPortType() {
        return new SakOgBehandlingPortTypeMock();

    }

    @Bean
    public SporsmalOgSvarPortType sporsmalOgSvarService() {
        return null;
    }

    @Bean
    public JaxWsProxyFactoryBean sporsmalOgSvarPortTypeFactory() {
        return null;
    }

    @Bean
    public HenvendelsePortType henvendelseService() {
        return null;
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
