package no.nav.sbl.dialogarena.minehenvendelser.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static no.nav.sbl.dialogarena.webkomponent.footer.FooterPanel.FOOTER_FEIL_OG_MANGLER_URL;
import static no.nav.sbl.dialogarena.webkomponent.footer.FooterPanel.FOOTER_KONTAKT_URL;
import static no.nav.sbl.dialogarena.webkomponent.footer.FooterPanel.FOOTER_NETTSTEDSKART_URL;
import static no.nav.sbl.dialogarena.webkomponent.footer.FooterPanel.FOOTER_PERSONVERN_URL;
import static no.nav.sbl.dialogarena.webkomponent.footer.FooterPanel.FOOTER_TILGJENGELIGHET_URL;

/**
 * Konfigurasjonsklasse for footer
 */
@Configuration
public class FooterConfig {

    @Value("${minehenvendelser.footer.kontakt.url}")
    private String kontaktUrl;

    @Value("${minehenvendelser.footer.personvern.url}")
    private String personvernUrl;

    @Value("${minehenvendelser.footer.feilOgMangler.url}")
    private String feilOgManglerUrl;

    @Value("${minehenvendelser.footer.tilgjengelighet.url}")
    private String tilgjengelighetUrl;

    @Value("${minehenvendelser.footer.nettstedskart.url}")
    private String nettstedskartUrl;

    @Bean
    public Map<String, String> footerLinks() {
        Map<String, String> footerLinks = new HashMap<>();
        footerLinks.put(FOOTER_KONTAKT_URL, kontaktUrl);
        footerLinks.put(FOOTER_PERSONVERN_URL, personvernUrl);
        footerLinks.put(FOOTER_FEIL_OG_MANGLER_URL, feilOgManglerUrl);
        footerLinks.put(FOOTER_TILGJENGELIGHET_URL, tilgjengelighetUrl);
        footerLinks.put(FOOTER_NETTSTEDSKART_URL, nettstedskartUrl);
        return footerLinks;
    }

}