package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.security.ws.SecurityContextOutInterceptor;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import javax.xml.namespace.QName;
import java.net.URL;

@Profile("default")
@Configuration
@Import({WebContext.class})
public class ProductionApplicationContext implements SystemConfiguration {

    @Value("${henvendelser.ws.url}")
    protected URL endpoint;

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingsServicePort();
    }

    @Bean
    public HenvendelsesBehandlingPortType getHenvendelsesBehandlingPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceName(new QName(endpoint.getPath()));
        proxyFactoryBean.setEndpointName(new QName(endpoint.getPath()));
        proxyFactoryBean.setServiceClass(HenvendelsesBehandlingPortType.class);
        proxyFactoryBean.setAddress(endpoint.toString());
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());

        proxyFactoryBean.getOutInterceptors().add(new SecurityContextOutInterceptor());

        HenvendelsesBehandlingPortType henvendelsesBehandlingPortType = proxyFactoryBean.create(HenvendelsesBehandlingPortType.class);
        return henvendelsesBehandlingPortType;
    }

}
