package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;
import static no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils.createPortType;

@Configuration
public class Pingables {

    @Bean
    public Pingable cmsPing() {
        return new Pingable() {
            @Override
            public Ping ping() {
                HttpURLConnection connection = null;
                String navn = "ENONIC_CMS";
                try {
                    String url = System.getProperty("appres.cms.url");
                    connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setConnectTimeout(10000);
                    int statusCode = connection.getResponseCode();
                    return statusCode == HTTP_OK ? Ping.lyktes(navn) : Ping.feilet(navn, new RuntimeException("StatusCode: " + statusCode));
                } catch (IOException e) {
                    return Ping.feilet(navn, e);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }

            }
        };
    }

    @Bean
    public Pingable sendInnHenvendelsePing() {
        final String navn = "SEND_INN_HENVENDELSE";
        final SendInnHenvendelsePortType ws = createPortType(System.getProperty("send.inn.henvendelse.ws.url"),
                "classpath:SendInnHenvendelse.wsdl",
                SendInnHenvendelsePortType.class,
                false);
        return new Pingable() {
            @Override
            public Ping ping() {
                try {
                    ws.ping();
                    return Ping.lyktes(navn);
                } catch (Exception e) {
                    return Ping.feilet(navn, e);
                }
            }
        };
    }

    @Bean
    public Pingable henvendelsePing() {
        final String navn = "HENVENDELSE";
        final HenvendelsePortType ws = createPortType(System.getProperty("henvendelse.ws.url"),
                "classpath:Henvendelse.wsdl",
                HenvendelsePortType.class,
                false);
        return new Pingable() {
            @Override
            public Ping ping() {
                try {
                    ws.ping();
                    return Ping.lyktes(navn);
                } catch (Exception e) {
                    return Ping.feilet(navn, e);
                }
            }
        };
    }

    @Bean
    public Pingable innsynHenvendelsePing() {
        final String navn = "INNSYN_HENVENDELSE";
        final InnsynHenvendelsePortType ws = createPortType(System.getProperty("innsyn.henvendelse.ws.url"),
                "classpath:InnsynHenvendelse.wsdl",
                InnsynHenvendelsePortType.class,
                false);
        return new Pingable() {
            @Override
            public Ping ping() {
                try {
                    ws.ping();
                    return Ping.lyktes(navn);
                } catch (Exception e) {
                    return Ping.feilet(navn, e);
                }
            }
        };
    }

    @Bean
    public Pingable brukerprofilPing() {
        final String navn = "BRUKERPROFIL";
        final BrukerprofilPortType ws = createPortType(System.getProperty("brukerprofil.ws.url"),
                "classpath:brukerprofil/no/nav/tjeneste/virksomhet/brukerprofil/v1/Brukerprofil.wsdl",
                BrukerprofilPortType.class,
                false);
        return new Pingable() {
            @Override
            public Ping ping() {
                try {
                    ws.ping();
                    return Ping.lyktes(navn);
                } catch (Exception e) {
                    return Ping.feilet(navn, e);
                }
            }
        };
    }
}
