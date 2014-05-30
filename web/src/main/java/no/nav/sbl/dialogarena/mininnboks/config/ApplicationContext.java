package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLBehandlingsinformasjonV2;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSvar;
import no.nav.modig.cache.CacheConfig;
import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.sbl.dialogarena.mininnboks.WicketApplication;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.aktivitet.v2.HenvendelseAktivitetV2PortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.informasjon.v2.HenvendelseInformasjonV2PortType;
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
        return new HenvendelseService.Default(henvendelseInformasjonSSO(), henvendelseAktivitetSSO());
    }

    private static HenvendelseAktivitetV2PortType henvendelseAktivitetSSO() {
        return createPortType(System.getProperty("henvendelse.aktivitet.ws.url"),
                "classpath:HenvendelseAktivitetV2.wsdl",
                HenvendelseAktivitetV2PortType.class,
                true);
    }

    private static HenvendelseInformasjonV2PortType henvendelseInformasjonSSO() {
        return createPortType(System.getProperty("henvendelse.informasjon.ws.url"),
                "classpath:HenvendelseInformasjonV2.wsdl",
                HenvendelseInformasjonV2PortType.class,
                true);
    }

    @Bean
    public static HenvendelseAktivitetV2PortType henvendelseAktivitetSystemUser() {
        return createPortType(System.getProperty("henvendelse.aktivitet.ws.url"),
                "classpath:HenvendelseAktivitetV2.wsdl",
                HenvendelseAktivitetV2PortType.class,
                false);
    }

    @Bean
    public static HenvendelseInformasjonV2PortType henvendelseInformasjonSystemUser() {
        return createPortType(System.getProperty("henvendelse.informasjon.ws.url"),
                "classpath:HenvendelseInformasjonV2.wsdl",
                HenvendelseInformasjonV2PortType.class,
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
                XMLBehandlingsinformasjonV2.class,
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
