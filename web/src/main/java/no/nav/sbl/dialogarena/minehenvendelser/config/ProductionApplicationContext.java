package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.sbl.dialogarena.common.kodeverk.config.KodeverkConfig;
import no.nav.sbl.dialogarena.minehenvendelser.FoedselsnummerService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import java.net.URL;

import static org.apache.cxf.frontend.ClientProxy.getClient;

/**
 * Hovedprofil for produksjonsapplikasjonskonteksten
 */
@Profile("default")
@Configuration
@PropertySource("environment.properties")
@Import({WebContext.class, KodeverkConfig.class})
public class ProductionApplicationContext {

    private static final int WS_CLIENT_TIMEOUT = 10000;
    @Value("${henvendelser.ws.url}")
    protected URL endpoint;

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
        HenvendelsesBehandlingPortType henvendelsesBehandlingPortType = createHenvendelsesBehandlingClient();
        Client client = configureTimeout(henvendelsesBehandlingPortType);
        STSConfigurationUtility.configureStsForExternalSSO(client);
        return henvendelsesBehandlingPortType;
    }

    //Duplikat bønne for å få selftest til å kjøre med username-token (system-SAML). Skal fjernes når dette konfigureres gjennom wsdl
    @Bean(name = "selfTestHenvendelsesBehandlingPortType")
    public HenvendelsesBehandlingPortType selfTestHenvendelsesBehandlingPortType() {
        HenvendelsesBehandlingPortType henvendelsesBehandlingPortType = createHenvendelsesBehandlingClient();
        Client client = configureTimeout(henvendelsesBehandlingPortType);
        STSConfigurationUtility.configureStsForSystemUser(client);
        return henvendelsesBehandlingPortType;
    }

    private HenvendelsesBehandlingPortType createHenvendelsesBehandlingClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceClass(HenvendelsesBehandlingPortType.class);
        proxyFactoryBean.setWsdlLocation(classpathUrl("henvendelse/HenvendelsesBehandling.wsdl"));
        proxyFactoryBean.setAddress(endpoint.toString());
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        return proxyFactoryBean.create(HenvendelsesBehandlingPortType.class);
    }

    private Client configureTimeout(HenvendelsesBehandlingPortType henvendelsesBehandlingClient) {
        Client client = getClient(henvendelsesBehandlingClient);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.getClient().setReceiveTimeout(WS_CLIENT_TIMEOUT);
        httpConduit.getClient().setConnectionTimeout(WS_CLIENT_TIMEOUT);
        return client;
    }

    private String classpathUrl(String classpathLocation) {
        if (getClass().getClassLoader().getResource(classpathLocation) == null) {
            throw new RuntimeException(classpathLocation + " does not exist on classpath!");
        }
        return "classpath:" + classpathLocation;
    }

}
