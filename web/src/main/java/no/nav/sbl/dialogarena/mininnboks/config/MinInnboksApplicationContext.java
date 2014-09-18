package no.nav.sbl.dialogarena.mininnboks.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.cache.CacheConfig;
import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.modig.security.tilgangskontroll.policy.enrichers.EnvironmentRequestEnricher;
import no.nav.modig.security.tilgangskontroll.policy.enrichers.SecurityContextRequestEnricher;
import no.nav.modig.security.tilgangskontroll.policy.pdp.DecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.pdp.picketlink.PicketLinkDecisionPoint;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.pep.PEPImpl;
import no.nav.modig.wicket.services.HealthCheckService;
import no.nav.sbl.dialogarena.mininnboks.WicketApplication;
import no.nav.sbl.dialogarena.mininnboks.consumer.DiskresjonskodeService;
import no.nav.sbl.dialogarena.mininnboks.consumer.EpostService;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

@Configuration
@Import({CacheConfig.class, ContentConfig.class})
@ComponentScan(basePackageClasses = {DiskresjonskodeService.class})
public class MinInnboksApplicationContext implements ApplicationContextAware {

    @SuppressFBWarnings("MS_CANNOT_BE_FINAL")
    public static ApplicationContext context;

    private SendInnHenvendelsePortType sendInnHenvendelseSSO() {
        return createPortType(System.getProperty("send.inn.henvendelse.ws.url"),
                "classpath:SendInnHenvendelse.wsdl",
                SendInnHenvendelsePortType.class,
                true);
    }

    private HenvendelsePortType henvendelseSSO() {
        return createPortType(System.getProperty("henvendelse.ws.url"),
                "classpath:Henvendelse.wsdl",
                HenvendelsePortType.class,
                true);
    }

    private InnsynHenvendelsePortType innsynHenvendelseSSO() {
        return createPortType(System.getProperty("innsyn.henvendelse.ws.url"),
                "classpath:InnsynHenvendelse.wsdl",
                InnsynHenvendelsePortType.class,
                true);
    }

    private BrukerprofilPortType brukerprofilSSO() {
        return createPortType(System.getProperty("brukerprofil.ws.url"),
                "classpath:brukerprofil/no/nav/tjeneste/virksomhet/brukerprofil/v1/Brukerprofil.wsdl",
                BrukerprofilPortType.class,
                true);
    }

    private DiskresjonskodePortType diskresjonskodeSSO() {
        return createPortType(System.getProperty("diskresjonskode.ws.url"),
                "classpath:wsdl/Diskresjonskode.wsdl",
                DiskresjonskodePortType.class,
                true);
    }

    @Bean
    public static SendInnHenvendelsePortType sendInnHenvendelseSystemUser() {
        return createPortType(System.getProperty("send.inn.henvendelse.ws.url"),
                "classpath:SendInnHenvendelse.wsdl",
                SendInnHenvendelsePortType.class,
                false);
    }

    @Bean
    public HenvendelsePortType henvendelseSystemUser() {
        return createPortType(System.getProperty("henvendelse.ws.url"),
                "classpath:Henvendelse.wsdl",
                HenvendelsePortType.class,
                false);
    }

    @Bean
    public InnsynHenvendelsePortType innsynHenvendelseSystemUser() {
        return createPortType(System.getProperty("innsyn.henvendelse.ws.url"),
                "classpath:InnsynHenvendelse.wsdl",
                InnsynHenvendelsePortType.class,
                false);
    }

    @Bean
    public BrukerprofilPortType brukerprofilSystemUser() {
        return createPortType(System.getProperty("brukerprofil.ws.url"),
                "classpath:brukerprofil/no/nav/tjeneste/virksomhet/brukerprofil/v1/Brukerprofil.wsdl",
                BrukerprofilPortType.class,
                false);
    }

    @Bean
    public WicketApplication wicket() {
        return new WicketApplication();
    }

    @Bean
    public HenvendelseService henvendelseService() {
        return new HenvendelseService.Default(henvendelseSSO(), sendInnHenvendelseSSO(), innsynHenvendelseSSO());
    }

    @Bean
    public EpostService epostService() {
        return new EpostService.Default(brukerprofilSSO());
    }

    @Bean
    public DiskresjonskodeService diskresjonskodeService() {
        return new DiskresjonskodeService.Default(diskresjonskodeSSO());
    }

    @Bean
    public HealthCheckService healthCheck() {
        return new HealthCheckService();
    }

    @Bean
    public EnforcementPoint pep() throws IOException {
        PEPImpl pep = new PEPImpl(pdp());
        pep.setRequestEnrichers(Arrays.asList(new SecurityContextRequestEnricher(), new EnvironmentRequestEnricher()));
        return pep;
    }

    /*
     * PDP (Policy Decision Point) inneholder regelsett for tilgang, og avgjør hvorvidt bruker får tilgang. I første omgang vil
     * PDP være en integrert del av applikasjonen, men det er mulig at PDP vil trekkes ut som en tjeneste senere.
     */
    @Bean
    public DecisionPoint pdp() throws IOException {
        return new PicketLinkDecisionPoint(new ClassPathResource("pdp/policy-config.xml").getURL());
    }

    @Override
    @SuppressFBWarnings("ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD")
    public void setApplicationContext(ApplicationContext applicationContext) {
        MinInnboksApplicationContext.context = applicationContext;
    }

    private static <T> T createPortType(String address, String wsdlUrl, Class<T> serviceClass, boolean externalService) {
        JaxWsProxyFactoryBean proxy = new JaxWsProxyFactoryBean();
        proxy.setWsdlURL(wsdlUrl);
        proxy.setAddress(address);
        proxy.setServiceClass(serviceClass);
        proxy.getFeatures().addAll(Arrays.asList(new WSAddressingFeature(), new LoggingFeature()));
        proxy.setProperties(new HashMap<String, Object>());
        proxy.getProperties().put("jaxb.additionalContextClasses", new Class[]{
                XMLHenvendelse.class,
                XMLMetadataListe.class,
                XMLMeldingFraBruker.class,
                XMLMeldingTilBruker.class});

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
