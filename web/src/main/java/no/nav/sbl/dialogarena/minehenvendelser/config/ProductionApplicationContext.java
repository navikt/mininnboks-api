package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.security.ws.SecurityContextOutInterceptor;
import no.nav.sbl.dialogarena.common.kodeverk.config.KodeverkConfig;
import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdSecurityContext;
import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import javax.xml.namespace.QName;
import java.net.URL;

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
    public HenvendelsesBehandlingPortType getHenvendelsesBehandlingPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceClass(HenvendelsesBehandlingPortType.class);
        proxyFactoryBean.setServiceName(new QName(endpoint.getPath()));
        proxyFactoryBean.setEndpointName(new QName(endpoint.getPath()));
        proxyFactoryBean.setAddress(endpoint.toString());
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());

        proxyFactoryBean.getOutInterceptors().add(new SecurityContextOutInterceptor());

        HenvendelsesBehandlingPortType henvendelsesBehandlingPortType = proxyFactoryBean.create(HenvendelsesBehandlingPortType.class);
        Client client = ClientProxy.getClient(henvendelsesBehandlingPortType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.getClient().setReceiveTimeout(WS_CLIENT_TIMEOUT);
        return henvendelsesBehandlingPortType;
    }

    @Bean
    public AktoerIdService aktoerIdService() {
        return new AktoerIdSecurityContext();
    }

}
