package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.modig.cache.CacheConfig;
import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.sbl.dialogarena.mininnboks.WicketApplication;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmal.v1.SporsmalinnsendingPortType;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

@Configuration
@Import({CacheConfig.class, ContentConfig.class})
public class ApplicationContext {

    @Bean
    public WicketApplication wicket() {
        return new WicketApplication();
    }

    @Bean
    public HenvendelseService henvendelseService() {
        return new HenvendelseService.Default(henvendelsesSSO(), sporsmalinnsendingSSO());
    }

    private static SporsmalinnsendingPortType sporsmalinnsendingSSO() {
        return createPortType(System.getProperty("henvendelse.spsminnsending.ws.url"),
                "classpath:Sporsmalinnsending.wsdl",
                SporsmalinnsendingPortType.class,
                true);
    }

    private static HenvendelseMeldingerPortType henvendelsesSSO() {
        return createPortType(System.getProperty("henvendelse.meldinger.ws.url"),
                "classpath:no/nav/tjeneste/domene/brukerdialog/henvendelsemeldinger/v1/Meldinger.wsdl",
                HenvendelseMeldingerPortType.class,
                true);
    }

    @Bean
    public static SporsmalinnsendingPortType sporsmalinnsendingSystemUser() {
        return createPortType(System.getProperty("henvendelse.spsminnsending.ws.url"),
                "classpath:Sporsmalinnsending.wsdl",
                SporsmalinnsendingPortType.class,
                false);
    }

    @Bean
    public static HenvendelseMeldingerPortType henvendelsesSystemUser() {
        return createPortType(System.getProperty("henvendelse.meldinger.ws.url"),
                "classpath:no/nav/tjeneste/domene/brukerdialog/henvendelsemeldinger/v1/Meldinger.wsdl",
                HenvendelseMeldingerPortType.class,
                false);
    }

    private static <T> T createPortType(String address, String wsdlUrl, Class<T> serviceClass, boolean externalService) {
        JaxWsProxyFactoryBean proxy = new JaxWsProxyFactoryBean();
        proxy.getFeatures().addAll(Arrays.asList(new WSAddressingFeature(), new LoggingFeature()));
        proxy.setServiceClass(serviceClass);
        proxy.setAddress(address);
        proxy.setWsdlURL(wsdlUrl);
        T portType = proxy.create(serviceClass);
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.setTlsClientParameters(new TLSClientParameters());
        if (Boolean.valueOf(System.getProperty("disable.ssl.cn.check", "false"))) {
            httpConduit.getTlsClientParameters().setDisableCNCheck(true);
        }
        if (externalService) {
            STSConfigurationUtility.configureStsForExternalSSO(client);
        } else {
            STSConfigurationUtility.configureStsForSystemUser(client);
        }
        return portType;
    }

}
