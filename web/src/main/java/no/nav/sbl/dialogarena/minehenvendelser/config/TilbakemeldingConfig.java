package no.nav.sbl.dialogarena.minehenvendelser.config;

import static no.nav.sbl.dialogarena.minehenvendelser.ApplicationConstants.APPLICATION_NAME;

import java.net.MalformedURLException;
import java.net.URL;

import no.nav.sbl.dialogarena.webkomponent.tilbakemelding.service.Epostsender;
import no.nav.sbl.dialogarena.webkomponent.tilbakemelding.service.TilbakemeldingService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TilbakemeldingConfig {

    @Value("${tilbakemelding.email.address}")
    private String email;

    @Value("${tilbakemelding.smtp.host}")
    private URL host;

    @Value("${tilbakemelding.smtp.port}")
    private int port;

    @Value("${panel.tilbakemelding.enabled:true}")
    private String isEnabled;

    @Bean
    public Boolean tilbakemeldingEnabled() {
        return isEnabled.equalsIgnoreCase("false") ? false : true;
    }

    @Bean
    public TilbakemeldingService tilbakemeldingService() throws MalformedURLException {
        return new Epostsender(host.getHost(), port, APPLICATION_NAME, email);
    }

}
