package no.nav.sbl.dialogarena.minehenvendelser.consumer.config;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingServiceFilebased;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class ApplicationContextConsumer {

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan(new String[] {"no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling"});
        jaxb2Marshaller.setSchema(new ClassPathResource("xsd/behandlingsinformasjon.xsd"));
        return jaxb2Marshaller;
    }

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingServiceFilebased();
    }

}
