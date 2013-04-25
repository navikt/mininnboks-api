package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.BehandlingResponseMarshaller;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@Import(ConsumerContext.class)
public class ConsumerTestContext {

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer() {
        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource("environment-test.properties"));
        return placeholderConfigurer;
    }

    @Bean
    public BehandlingResponseMarshaller behandlingResponseMarshaller(){
        return new BehandlingResponseMarshaller(jaxb2Marshaller());
    }

    @Bean
    public MockData mockData() {
        MockData mockData = new MockData();
        return mockData;
    }


    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(
                no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.ObjectFactory.class,
                no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.ObjectFactory.class,
                no.nav.sbl.dialogarena.minehenvendelser.consumer.soap.ObjectFactory.class);
        return marshaller;
    }

}
