package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.apiapp.selftest.Helsesjekk;
import no.nav.apiapp.selftest.HelsesjekkMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.metrics.MetricsFactory;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.util.EnvironmentUtils;
import no.nav.sbl.util.fn.UnsafeRunnable;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v3.BrukerprofilV3;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils.createPortType;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
public class ServiceConfig {

    public static final String INNSYN_HENVENDELSE_WS_URL = "innsyn.henvendelse.ws.url";
    public static final String HENVENDELSE_WS_URL = "henvendelse.ws.url";
    public static final String SEND_INN_HENVENDELSE_WS_URL = "send.inn.henvendelse.ws.url";
    public static final String BRUKERPROFIL_V_3_URL = "brukerprofil.v3.url";

    @Bean
    public PersonService personService() {
        return new PersonService.Default(createBrukerprofilPort().portType);
    }

    @Bean
    public Pingable personServicePing() {
        return createBrukerprofilPort().helsesjekk;
    }

    private PortTypeUtils.PortType<BrukerprofilV3> createBrukerprofilPort() {
        return createPortType(getRequiredProperty(BRUKERPROFIL_V_3_URL),
                "",
                BrukerprofilV3.class,
                true,
                BrukerprofilV3::ping
        );
    }

    @Bean
    public HenvendelseService henvendelseService(PersonService personService) {
        return new HenvendelseService.Default(
                henvendelseSSO(),
                sendInnHenvendelseSSO(),
                innsynHenvendelseSSO(),
                personService
        );
    }

    private SendInnHenvendelsePortType sendInnHenvendelseSSO() {
        return henvendelseCXF(SendInnHenvendelsePortType.class, SEND_INN_HENVENDELSE_WS_URL, "classpath:SendInnHenvendelse.wsdl");
    }

    @Bean
    public Pingable sendInnHenvendelsePing() {
        return createPortType(
                EnvironmentUtils.getRequiredProperty(SEND_INN_HENVENDELSE_WS_URL),
                "classpath:SendInnHenvendelse.wsdl",
                SendInnHenvendelsePortType.class,
                false,
                SendInnHenvendelsePortType::ping
        ).helsesjekk;
    }

    private HenvendelsePortType henvendelseSSO() {
        return henvendelseCXF(HenvendelsePortType.class, HENVENDELSE_WS_URL, "classpath:Henvendelse.wsdl");
    }

    @Bean
    public Pingable henvendelsePing() {
        return createPortType(
                getRequiredProperty(HENVENDELSE_WS_URL),
                "classpath:Henvendelse.wsdl",
                HenvendelsePortType.class,
                false,
                HenvendelsePortType::ping
        ).helsesjekk;
    }

    private InnsynHenvendelsePortType innsynHenvendelseSSO() {
        return henvendelseCXF(InnsynHenvendelsePortType.class, INNSYN_HENVENDELSE_WS_URL, "classpath:InnsynHenvendelse.wsdl");
    }

    @Bean
    public Pingable innsynHenvendelsePing() {
        return createPortType(
                getRequiredProperty(INNSYN_HENVENDELSE_WS_URL),
                "classpath:InnsynHenvendelse.wsdl",
                InnsynHenvendelsePortType.class,
                false,
                InnsynHenvendelsePortType::ping
        ).helsesjekk;
    }

    private <T> T henvendelseCXF(Class<T> serviceClass, String addressProperty, String wsdl) {
        return new CXFClient<>(serviceClass)
                .configureStsForExternalSSO()
                .address(getRequiredProperty(addressProperty))
                .wsdl(wsdl)
                .withProperty("jaxb.additionalContextClasses", new Class[]{
                        XMLHenvendelse.class,
                        XMLMetadataListe.class,
                        XMLMeldingFraBruker.class,
                        XMLMeldingTilBruker.class
                })
                .build();
    }
}
