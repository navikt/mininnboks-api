package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
import no.nav.modig.cache.CacheConfig;
import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.sbl.dialogarena.mininnboks.WicketApplication;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
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
import java.util.HashMap;

@Configuration
@Import({CacheConfig.class, ContentConfig.class})
public class ApplicationContext {

    @Bean
    public WicketApplication wicket() {
        return new WicketApplication();
    }

    @Bean
    public HenvendelseService henvendelseService() {
        return new HenvendelseService.Default(henvendelseSSO(), sendInnHenvendelseSSO(), innsynHenvendelseSSO());
    }

    private static HenvendelsePortType henvendelseSSO() {
        return createPortType(System.getProperty("henvendelse.ws.url"),
                "classpath:Henvendelse.wsdl",
                HenvendelsePortType.class,
                true);
    }

    private static SendInnHenvendelsePortType sendInnHenvendelseSSO() {
        return createPortType(System.getProperty("send.inn.henvendelse.ws.url"),
                "classpath:SendInnHenvendelse.wsdl",
                SendInnHenvendelsePortType.class,
                true);
    }

    private static InnsynHenvendelsePortType innsynHenvendelseSSO() {
        return createPortType(System.getProperty("innsyn.henvendelse.ws.url"),
                "classpath:InnsynHenvendelse.wsdl",
                InnsynHenvendelsePortType.class,
                true);
    }

    @Bean
    public static HenvendelsePortType henvendelseSystemUser() {
        return createPortType(System.getProperty("henvendelse.ws.url"),
                "classpath:Henvendelse.wsdl",
                HenvendelsePortType.class,
                false);
    }

    @Bean
    public static SendInnHenvendelsePortType sendInnHenvendelseSystemUser() {
        return createPortType(System.getProperty("send.inn.henvendelse.ws.url"),
                "classpath:SendInnHenvendelse.wsdl",
                SendInnHenvendelsePortType.class,
                false);
    }

    @Bean
    public static InnsynHenvendelsePortType innsynHenvendelseSystemUser() {
        return createPortType(System.getProperty("innsyn.henvendelse.ws.url"),
                "classpath:InnsynHenvendelse.wsdl",
                InnsynHenvendelsePortType.class,
                false);
    }

    private static <T> T createPortType(String address, String wsdlUrl, Class<T> serviceClass, boolean externalService) {
        JaxWsProxyFactoryBean proxy = new JaxWsProxyFactoryBean();
        proxy.setWsdlURL(wsdlUrl);
        proxy.setAddress(address);
        proxy.setServiceClass(serviceClass);
        proxy.getFeatures().addAll(Arrays.asList(new WSAddressingFeature(), new LoggingFeature()));
        proxy.setProperties(new HashMap<String, Object>());
        proxy.getProperties().put("jaxb.additionalContextClasses", new Class[]{
                XMLBehandlingsinformasjon.class,
                XMLMetadataListe.class,
                XMLSporsmal.class,
                XMLSvar.class,
                XMLReferat.class});

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
