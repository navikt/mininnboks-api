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
    private String tilbakemeldingEmail;

    @Value("${tilbakemelding.smtp.url}")
    private URL tilbakemeldingUrl;

    @Value("${panel.tilbakemelding.enabled:true}")
    private String tilbakemeldingEnabled;

    @Bean
    public Boolean tilbakemeldingEnabled() {
        return tilbakemeldingEnabled.equalsIgnoreCase("false") ? false : true;
    }

    @Bean
    public TilbakemeldingService tilbakemeldingService() throws MalformedURLException {
        String host = tilbakemeldingUrl.getHost();
        int port = tilbakemeldingUrl.getPort();
        return new Epostsender(host, port, APPLICATION_NAME, tilbakemeldingEmail);
    }

}
