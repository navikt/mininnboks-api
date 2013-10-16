package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.cache.CacheConfig;
import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.content.ValuesFromContentWithResourceBundleFallback;
import no.nav.modig.content.enonic.HttpContentRetriever;
import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.WicketApplication;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.HenvendelseService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.HenvendelseMeldingerPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmal.v1.SporsmalinnsendingPortType;
import no.nav.tjeneste.virksomhet.behandlebrukerprofil.v1.BehandleBrukerprofilPortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Import(CacheConfig.class)
public class ApplicationContext {

    @Bean
    public ContentRetriever contentRetriever() {
        // Egen bønne for å hooke opp @Cachable
        return new HttpContentRetriever();
    }

    @Bean
    public CmsContentRetriever cmsContentRetriever(ContentRetriever contentRetriever) throws URISyntaxException {
        String cmsBaseUrl = System.getProperty("dialogarena.cms.url");
        Map<String, URI> uris = new HashMap<>();
        uris.put("nb", new URI(cmsBaseUrl + "/site/16/sbl-webkomponenter/nb/tekster"));
        ValueRetriever valueRetriever = new ValuesFromContentWithResourceBundleFallback("content.sbl-webkomponenter", contentRetriever, uris, "nb");
        CmsContentRetriever cmsContentRetriever = new CmsContentRetriever();
        cmsContentRetriever.setDefaultLocale("nb");
        cmsContentRetriever.setTeksterRetriever(valueRetriever);
        return cmsContentRetriever;
    }

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
    public static BrukerprofilPortType brukerprofilSSO() {
        return createPortType(System.getProperty("brukerprofil.ws.url"),
                "classpath:brukerprofil/no/nav/tjeneste/virksomhet/brukerprofil/v1/Brukerprofil.wsdl",
                BrukerprofilPortType.class,
                true);
    }

    @Bean
    public static BehandleBrukerprofilPortType behandleBrukerprofilSSO() {
        return createPortType(System.getProperty("behandlebrukerprofil.ws.url"),
                "classpath:behandleBrukerprofil/no/nav/tjeneste/virksomhet/behandleBrukerprofil/v1/BehandleBrukerprofil.wsdl",
                BehandleBrukerprofilPortType.class,
                true);
    }

    @Bean
    public static BrukerprofilPortType brukerprofilSystemUser() {
        return createPortType(System.getProperty("brukerprofil.ws.url"),
                "classpath:brukerprofil/no/nav/tjeneste/virksomhet/brukerprofil/v1/Brukerprofil.wsdl",
                BrukerprofilPortType.class,
                false);
    }

    @Bean
    public static BehandleBrukerprofilPortType behandleBrukerprofilSystemUser() {
        return createPortType(System.getProperty("behandlebrukerprofil.ws.url"),
                "classpath:behandleBrukerprofil/no/nav/tjeneste/virksomhet/behandleBrukerprofil/v1/BehandleBrukerprofil.wsdl",
                BehandleBrukerprofilPortType.class,
                false);
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
