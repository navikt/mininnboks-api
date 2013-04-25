package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServiceImpl;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Springkontekst for consumermodulen
 */
@Configuration
public class ConsumerContext {

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingsServiceImpl();
    }

    @Bean
    public HenvendelsesBehandlingPortType jaxWsClientFactoryBean(){
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceClass(HenvendelsesBehandlingPortType.class);
        proxyFactoryBean.setAddress("DUMMY_ENDPOINT");
        return proxyFactoryBean.create(HenvendelsesBehandlingPortType.class);
    }

}
