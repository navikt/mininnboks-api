package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService;
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlServiceImpl;
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider;
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.ApigwRequestFilter;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.rest.RestUtils;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;

import static no.nav.sbl.dialogarena.mininnboks.config.ApplicationConfig.*;
import static no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils.createPortType;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class ServiceConfig {

    public static final String SERVICEGATEWAY_URL = "SERVICEGATEWAY_URL";
    public static final String INNSYN_HENVENDELSE_WS_URL = "innsyn.henvendelse.ws.url";
    public static final String HENVENDELSE_WS_URL = "henvendelse.ws.url";
    public static final String SEND_INN_HENVENDELSE_WS_URL = "send.inn.henvendelse.ws.url";
    public static final String BRUKERPROFIL_V_3_URL = "brukerprofil.v3.url";

    public static final String PDL_API_URL = "PDL_API_URL";
    public static final String PDL_API_APIKEY = "PDL_API_APIKEY";

    public static final String STS_APIKEY = "STS_APIKEY";
    public static final String STS_TOKENENDPOINT_URL = "STS_TOKENENDPOINT_URL";

    @Bean
    public PersonService personService() {
        return new PersonService.Default(brukerprofil().port);
    }

    @Bean
    public Pingable personServicePing() {
        return brukerprofil().helsesjekk;
    }

    private PortTypeUtils.PortType<BrukerprofilV3> brukerprofil() {
        return createPortType(getRequiredProperty(BRUKERPROFIL_V_3_URL),
                "",
                BrukerprofilV3.class,
                BrukerprofilV3::ping
        );
    }

    @Bean
    public HenvendelseService henvendelseService(PersonService personService) {
        return new HenvendelseService.Default(
                henvendelse().port,
                sendInnHenvendelse().port,
                innsynHenvendesle().port,
                personService
        );
    }

    @Bean
    public SystemuserTokenProvider systemUserTokenProvider() {
        String stsApikey = EnvironmentUtils.getRequiredProperty(STS_APIKEY);
        Client client =  RestUtils.createClient().register(new ApigwRequestFilter(stsApikey));

        return SystemuserTokenProvider.fromTokenEndpoint(
                getRequiredProperty(STS_TOKENENDPOINT_URL),
                getRequiredProperty(FSS_SRVMININNBOKS_USERNAME),
                getRequiredProperty(FSS_SRVMININNBOKS_PASSWORD),
                client
        );
    }

    @Bean
    public PdlService pdlService(SystemuserTokenProvider stsService) {
        String pdlapiApikey = EnvironmentUtils.getRequiredProperty(PDL_API_APIKEY);
        Client client =  RestUtils.createClient().register(new ApigwRequestFilter(pdlapiApikey));

        return new PdlServiceImpl(client, stsService);
    }

    @Bean
    public Pingable pdlPing(PdlService pdlService) {
        return pdlService.getHelsesjekk();
    }

    @Bean
    public Pingable sendInnHenvendelsePing() {
        return sendInnHenvendelse().helsesjekk;
    }

    private PortTypeUtils.PortType<SendInnHenvendelsePortType> sendInnHenvendelse() {
        return createPortType(
                EnvironmentUtils.getRequiredProperty(SEND_INN_HENVENDELSE_WS_URL),
                "classpath:wsdl/SendInnHenvendelse.wsdl",
                SendInnHenvendelsePortType.class,
                SendInnHenvendelsePortType::ping
        );
    }

    @Bean
    public Pingable henvendelsePing() {
        return henvendelse().helsesjekk;
    }

    private PortTypeUtils.PortType<HenvendelsePortType> henvendelse() {
        return createPortType(
                getRequiredProperty(HENVENDELSE_WS_URL),
                "classpath:wsdl/Henvendelse.wsdl",
                HenvendelsePortType.class,
                HenvendelsePortType::ping
        );
    }

    @Bean
    public Pingable innsynHenvendelsePing() {
        return innsynHenvendesle().helsesjekk;
    }

    private PortTypeUtils.PortType<InnsynHenvendelsePortType> innsynHenvendesle() {
        return createPortType(
                getRequiredProperty(INNSYN_HENVENDELSE_WS_URL),
                "classpath:wsdl/InnsynHenvendelse.wsdl",
                InnsynHenvendelsePortType.class,
                InnsynHenvendelsePortType::ping
        );
    }
}
