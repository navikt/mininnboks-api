package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils.createPortType;

@Configuration
public class HenvendelseServiceConfig {

    @Bean
    public HenvendelseService henvendelseService() {
        return new HenvendelseService.Default(henvendelseSSO(), sendInnHenvendelseSSO(), innsynHenvendelseSSO());
    }

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
}
