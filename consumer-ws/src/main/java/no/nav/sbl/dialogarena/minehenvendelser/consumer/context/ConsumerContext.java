package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.xml.namespace.QName;

/**
 * Springkontekst for consumermodulen
 */
@Configuration
public class ConsumerContext {

    @Value("${henvendelser.ws.url}")
    private String endpoint;

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer() {
        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource("environment-prod.properties"));
        return placeholderConfigurer;
    }

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingsServicePort();
    }

    @Bean
    public HenvendelsesBehandlingPortType jaxWsClientFactoryBean() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceName(new QName("https://d26jbsl00007.test.local:8443/henvendelse/services/informasjon/v1/HenvendelsesBehandlingService/", "henvendelsesbehandlingservice"));
        proxyFactoryBean.setEndpointName(new QName("https://d26jbsl00007.test.local:8443/henvendelse/services/informasjon/v1/HenvendelsesBehandlingService/", "henvendelsesbehandlingservice"));
        proxyFactoryBean.setServiceClass(HenvendelsesBehandlingPortType.class);
        proxyFactoryBean.setAddress(endpoint);
        proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
        proxyFactoryBean.getFeatures().add(new LoggingFeature());

//        proxyFactoryBean.getOutInterceptors().add(new SecurityContextOutInterceptor());

        HenvendelsesBehandlingPortType henvendelsesBehandlingPortType = proxyFactoryBean.create(HenvendelsesBehandlingPortType.class);
        return henvendelsesBehandlingPortType;
    }


}
