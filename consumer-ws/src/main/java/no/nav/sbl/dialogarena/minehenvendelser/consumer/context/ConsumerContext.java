package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingServiceDefault;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class ConsumerContext {

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingServiceDefault();
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan(new String[]{"no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling"});
        jaxb2Marshaller.setSchema(new ClassPathResource("xsd/henvendelse.xsd"));
        return jaxb2Marshaller;
    }

}
