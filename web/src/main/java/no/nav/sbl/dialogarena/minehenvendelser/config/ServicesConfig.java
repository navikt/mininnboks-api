package no.nav.sbl.dialogarena.minehenvendelser.config;

/**
 * Spring config for jaxws webservices
 */

import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.HenvendelseSporsmalOgSvarPortType;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static org.apache.cxf.frontend.ClientProxy.getClient;

@Configuration
public class ServicesConfig {

    @Inject
    private JaxWsFeatures jaxwsFeatures;

    @Value("${henvendelser.webservice.sporsmal.url}")
    protected String spmSvarEndpoint;

    @Bean
    public HenvendelseSporsmalOgSvarPortType sporsmalOgSvarService() {
        HenvendelseSporsmalOgSvarPortType henvendelseSporsmalOgSvarPortType =
                henvendelseSporsmalOgSvarPortTypeFactory().create(HenvendelseSporsmalOgSvarPortType.class);
        Client client = getClient(henvendelseSporsmalOgSvarPortType);
        STSConfigurationUtility.configureStsForExternalSSO(client);
        return henvendelseSporsmalOgSvarPortType;
    }

    @Bean
    public JaxWsProxyFactoryBean henvendelseSporsmalOgSvarPortTypeFactory() {
        JaxWsProxyFactoryBean jaxwsClient = commonJaxWsConfig();
        jaxwsClient.setServiceClass(HenvendelseSporsmalOgSvarPortType.class);
        jaxwsClient.setAddress(spmSvarEndpoint);
        jaxwsClient.setWsdlURL(classpathUrl("HenvendelseSporsmalOgSvar.wsdl"));
        return jaxwsClient;
    }

    @Bean
    public JaxWsProxyFactoryBean commonJaxWsConfig() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        Map<String, Object> properties = new HashMap<>();
        properties.put("schema-validation-enabled", true);
        factoryBean.setProperties(properties);
        factoryBean.getFeatures().addAll(jaxwsFeatures.jaxwsFeatures());
        return factoryBean;
    }

    private String classpathUrl(String classpathLocation) {
        if (getClass().getClassLoader().getResource(classpathLocation) == null) {
            throw new RuntimeException(classpathLocation + " does not exist on classpath!");
        }
        return "classpath:" + classpathLocation;
    }

}
