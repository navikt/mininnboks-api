package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.modig.cache.CacheConfig;
import no.nav.modig.security.ws.AbstractSAMLOutInterceptor;
import no.nav.modig.security.ws.UserSAMLOutInterceptor;
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import no.nav.tjeneste.virksomhet.person.v2.PersonV2;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.xml.namespace.QName;

import static no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils.createPortType;

@Configuration
@Import({CacheConfig.class, ContentConfig.class, HenvendelseServiceConfig.class, Pingables.class})
public class MinInnboksApplicationContext {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public PersonService personService() {
        return new PersonService.Default(brukerprofilSSO(), personV2(new UserSAMLOutInterceptor()));
    }


    private BrukerprofilPortType brukerprofilSSO() {
        return createPortType(System.getProperty("brukerprofil.ws.url"),
                "classpath:brukerprofil/no/nav/tjeneste/virksomhet/brukerprofil/v1/Brukerprofil.wsdl",
                BrukerprofilPortType.class,
                true);
    }

    static PersonV2 personV2(AbstractSAMLOutInterceptor samlOutInterceptor) {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();

        factoryBean.setWsdlURL("classpath:no/nav/tjeneste/virksomhet/person/v2/Binding.wsdl");
        factoryBean.setServiceName(new QName("http://nav.no/tjeneste/virksomhet/person/v2/Binding", "Person_v2"));
        factoryBean.setEndpointName(new QName("http://nav.no/tjeneste/virksomhet/person/v2/Binding", "Person_v2Port"));
        factoryBean.setAddress(System.getProperty("person.v2.ws.url"));
        factoryBean.setServiceClass(PersonV2.class);
        factoryBean.getFeatures().add(new WSAddressingFeature());
        factoryBean.getFeatures().add(new LoggingFeature());
        factoryBean.getOutInterceptors().add(samlOutInterceptor);

        return factoryBean.create(PersonV2.class);
    }
}
