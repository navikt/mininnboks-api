package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.metrics.MetricsFactory;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HenvendelseServiceConfig {

    @Bean
    public HenvendelseService henvendelseService(PersonService personService) {
        return new HenvendelseService.Default(henvendelseSSO(), sendInnHenvendelseSSO(), innsynHenvendelseSSO(), personService);
    }

    private SendInnHenvendelsePortType sendInnHenvendelseSSO() {
        SendInnHenvendelsePortType factory = henvendelseCXF(SendInnHenvendelsePortType.class, "send.inn.henvendelse.ws.url", "classpath:SendInnHenvendelse.wsdl");
        return MetricsFactory.createTimerProxyForWebService("SendInnHenvendelse", factory, SendInnHenvendelsePortType.class);
    }

    private HenvendelsePortType henvendelseSSO() {
        HenvendelsePortType factory = henvendelseCXF(HenvendelsePortType.class, "henvendelse.ws.url", "classpath:Henvendelse.wsdl");
        return MetricsFactory.createTimerProxyForWebService("Henvendelse", factory, HenvendelsePortType.class);
    }

    private InnsynHenvendelsePortType innsynHenvendelseSSO() {
        InnsynHenvendelsePortType factory = henvendelseCXF(InnsynHenvendelsePortType.class, "innsyn.henvendelse.ws.url", "classpath:InnsynHenvendelse.wsdl");
        return MetricsFactory.createTimerProxyForWebService("InnsynHenvendelse", factory, InnsynHenvendelsePortType.class);
    }

    private <T> T henvendelseCXF(Class<T> serviceClass, String addressProperty, String wsdl) {
        return new CXFClient<>(serviceClass)
                .configureStsForExternalSSO()
                .address(System.getProperty(addressProperty))
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
