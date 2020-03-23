package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider;
import no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService;
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.ApigwRequestFilter;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.rest.RestUtils;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import org.eclipse.jetty.util.annotation.Name;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

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
    public Client pdlClient() {
        String pdlapiApikey = EnvironmentUtils.getRequiredProperty(PDL_API_APIKEY);
        return RestUtils.createClient().register(new ApigwRequestFilter(pdlapiApikey));
    }

    @Bean
    public PdlService pdlService(@Named("pdlClient") Client client, SystemUserTokenProvider stsService) {
        return new PdlService(client, stsService);
    }

    @Bean
    public Pingable pdlPing(Client pdlClient) {
        Pingable.Ping.PingMetadata metadata = new Pingable.Ping.PingMetadata(
                "pdl",
                EnvironmentUtils.getOptionalProperty(PDL_API_URL).orElse("NOT FOUND"),
                "Henter diskresjonskode",
                false
        );

        return () -> {
            try {
                String pdlapiUrl = EnvironmentUtils.getRequiredProperty(PDL_API_URL);
                Response response = pdlClient
                        .target(pdlapiUrl)
                        .request()
                        .options();

                if (response.getStatus() == 200) {
                    return Pingable.Ping.lyktes(metadata);
                } else {
                    return Pingable.Ping.feilet(metadata, "Fikk statuskode: " + response.getStatus());
                }
            } catch (Exception e) {
                return Pingable.Ping.feilet(metadata, e);
            }
        };
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
