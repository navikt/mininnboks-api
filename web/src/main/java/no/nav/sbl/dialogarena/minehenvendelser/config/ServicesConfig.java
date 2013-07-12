package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.sbl.dialogarena.minehenvendelser.FoedselsnummerService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.apache.cxf.frontend.ClientProxy.getClient;

/**
 * Spring config for jaxws webservices
 */

@Configuration
public class ServicesConfig {

    private static final int WS_CLIENT_TIMEOUT = 10000;

    @Inject
    private JaxWsFeatures jaxwsFeatures;

    @Value("${henvendelser.ws.url}")
    protected URL endpoint;

    @Value("${henvendelser.webservice.sporsmal.url}")
    protected String spmSvarEndpoint;

    @Bean
    public SporsmalOgSvarPortType sporsmalOgSvarService() {
        SporsmalOgSvarPortType sporsmalOgSvarPortType =
                sporsmalOgSvarPortTypeFactory().create(SporsmalOgSvarPortType.class);
        Client client = getClient(sporsmalOgSvarPortType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.setTlsClientParameters(jaxwsFeatures.tlsClientParameters());
        STSConfigurationUtility.configureStsForExternalSSO(client);
        return sporsmalOgSvarPortType;
    }

    @Bean
    public JaxWsProxyFactoryBean sporsmalOgSvarPortTypeFactory() {
        JaxWsProxyFactoryBean jaxwsClient = commonJaxWsConfig();
        jaxwsClient.setServiceClass(SporsmalOgSvarPortType.class);
        jaxwsClient.setAddress(spmSvarEndpoint);
        jaxwsClient.setWsdlURL(classpathUrl("SporsmalOgSvar.wsdl"));
        return jaxwsClient;
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

    // Denne kan ikke være en spring bean
    private JaxWsProxyFactoryBean commonJaxWsConfig() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        Map<String, Object> properties = new HashMap<>();
        properties.put("schema-validation-enabled", true);
        factoryBean.setProperties(properties);
        factoryBean.getFeatures().addAll(jaxwsFeatures.jaxwsFeatures());
        return factoryBean;
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

    private HenvendelsesBehandlingPortType createHenvendelsesBehandlingClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = commonJaxWsConfig();
        proxyFactoryBean.setServiceClass(HenvendelsesBehandlingPortType.class);
        proxyFactoryBean.setWsdlLocation(classpathUrl("HenvendelsesBehandling.wsdl"));
        proxyFactoryBean.setAddress(endpoint.toString());
        return proxyFactoryBean.create(HenvendelsesBehandlingPortType.class);
    }

}
