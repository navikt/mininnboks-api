package no.nav.sbl.dialogarena.minehenvendelser.provider.rs.config;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.sbl.dialogarena.minehenvendelser.FoedselsnummerService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.provider.rs.InnsendingerProvider;
import no.nav.sbl.dialogarena.minehenvendelser.provider.rs.InnsendingerService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static no.nav.modig.security.sts.utility.STSConfigurationUtility.configureStsForExternalSSO;
import static org.apache.cxf.frontend.ClientProxy.getClient;

@Configuration
public class RestServiceConfig {

    private static final int WS_CLIENT_TIMEOUT = 10000;

    @Value("${sakogbehandling.ws.url}")
    protected URL sakogbehandlingEndpoint;

    @Value("${henvendelser.ws.url}")
    protected  URL henvendelseEndpoint;

    @Inject
    private JaxWsFeatures jaxwsFeatures;

    @Bean
    public InnsendingerProvider innsendingerProvider() {
        return new InnsendingerProvider();
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
    public SakogbehandlingService sakogbehandlingService() {
        return new SakogbehandlingService();
    }

    @Bean
    public InnsendingerService innsendingerService() {
        return new InnsendingerService();
    }

    @Bean
    public SakOgBehandlingPortType sakOgBehandlingPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setWsdlLocation("sakOgBehandling/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/SakOgBehandling.wsdl");
        proxyFactoryBean.setAddress(sakogbehandlingEndpoint.toString());
        proxyFactoryBean.setServiceClass(SakOgBehandlingPortType.class);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());
        SakOgBehandlingPortType sakOgBehandlingPortType = proxyFactoryBean.create(SakOgBehandlingPortType.class);
        configureStsForExternalSSO(getClient(sakOgBehandlingPortType));
        return sakOgBehandlingPortType;
    }

    @Bean
    public CmsContentRetriever cmsContentRetriever() {
        return new CmsContentRetriever();
    }

    @Bean
    public HenvendelsesBehandlingPortType getHenvendelsesBehandlingPortType() {
        HenvendelsesBehandlingPortType henvendelsesBehandlingPortType = createHenvendelsesBehandlingClient();
        Client client = configureTimeout(henvendelsesBehandlingPortType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.setTlsClientParameters(jaxwsFeatures.tlsClientParameters());
        STSConfigurationUtility.configureStsForExternalSSO(client);
        return henvendelsesBehandlingPortType;
    }

    private Client configureTimeout(HenvendelsesBehandlingPortType henvendelsesBehandlingClient) {
        Client client = getClient(henvendelsesBehandlingClient);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.getClient().setReceiveTimeout(WS_CLIENT_TIMEOUT);
        httpConduit.getClient().setConnectionTimeout(WS_CLIENT_TIMEOUT);
        return client;
    }


    private HenvendelsesBehandlingPortType createHenvendelsesBehandlingClient() {
        JaxWsProxyFactoryBean proxyFactoryBean = commonJaxWsConfig();
        proxyFactoryBean.setServiceClass(HenvendelsesBehandlingPortType.class);
        proxyFactoryBean.setWsdlLocation("classpath:HenvendelsesBehandling.wsdl");
        proxyFactoryBean.setAddress(henvendelseEndpoint.toString());
        return proxyFactoryBean.create(HenvendelsesBehandlingPortType.class);
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
}
