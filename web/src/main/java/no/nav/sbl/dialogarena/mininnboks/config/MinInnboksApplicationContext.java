package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.modig.cache.CacheConfig;
import no.nav.modig.wicket.services.HealthCheckService;
import no.nav.sbl.dialogarena.mininnboks.WicketApplication;
import no.nav.sbl.dialogarena.mininnboks.consumer.EpostService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils.createPortType;

@Configuration
@Import({CacheConfig.class, ContentConfig.class, HenvendelseServiceConfig.class})
public class MinInnboksApplicationContext {

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    private BrukerprofilPortType brukerprofilSSO() {
        return createPortType(System.getProperty("brukerprofil.ws.url"),
                "classpath:brukerprofil/no/nav/tjeneste/virksomhet/brukerprofil/v1/Brukerprofil.wsdl",
                BrukerprofilPortType.class,
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
    public EpostService epostService() {
        return new EpostService.Default(brukerprofilSSO());
    }

    @Bean
    public HealthCheckService healthCheck() {
        return new HealthCheckService();
    }

}
