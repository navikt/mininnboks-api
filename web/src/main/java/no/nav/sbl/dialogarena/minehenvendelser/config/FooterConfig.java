package no.nav.sbl.dialogarena.minehenvendelser.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static no.nav.sbl.dialogarena.webkomponent.footer.FooterPanel.DIALOGARENA_FOOTER_BASEURL;

/**
 * Konfigurasjonsklasse for footer
 */
@Configuration
public class FooterConfig {

    @Value("${dialogarena.footer.url}")
    private String footerBaseUrl;

    @Bean
    public Map<String, String> footerLinks() {
        Map<String, String> footerLinks = new HashMap<>();
        footerLinks.put(DIALOGARENA_FOOTER_BASEURL, footerBaseUrl);
        return footerLinks;
    }

}