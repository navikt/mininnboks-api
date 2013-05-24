package no.nav.sbl.dialogarena.minehenvendelser.config;

import static no.nav.sbl.dialogarena.minehenvendelser.ApplicationConstants.APPLICATION_NAME;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.content.ValuesFromContentWithResourceBundleFallback;
import no.nav.modig.content.enonic.EnonicContentRetriever;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.CmsContentRetriever;
import no.nav.sbl.dialogarena.webkomponent.tilbakemelding.service.Epostsender;
import no.nav.sbl.dialogarena.webkomponent.tilbakemelding.service.TilbakemeldingService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Produksjonskontekst for webmodulen
 */
@Configuration
public class WebContext {

    private static final String DEFAULT_LOCALE = "nb";
    private static final String INNHOLDSTEKSTER_NB_NO_REMOTE = "/site/16/minehenvendelser/nb/tekster";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "content.innholdstekster";

    @Value("${minehenvendelser.cms.url}")
    private String cmsBaseUrl;

    @Value("${panel.tilbakemelding.enabled:true}")
    private String tilbakemeldingEnabled;

    @Bean
    public ValueRetriever siteContentRetriever() throws URISyntaxException {
        Map<String, URI> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE, new URI(cmsBaseUrl + INNHOLDSTEKSTER_NB_NO_REMOTE));
        return new ValuesFromContentWithResourceBundleFallback(INNHOLDSTEKSTER_NB_NO_LOCAL, enonicContentRetriever(), uris, DEFAULT_LOCALE);
    }

    @Bean
    public Boolean tilbakemeldingEnabled() {
        return tilbakemeldingEnabled.equalsIgnoreCase("false") ? false : true;
    }

    @Bean
    public TilbakemeldingService tilbakemeldingService() throws MalformedURLException {
        //TODO: endres til å benytte konfigurasjon fra env-config så fort ressurs for SMTP er på plass (MODTP-700)
        URL tilbakemeldingUrl = new URL("http://localhost:25");
        String host = tilbakemeldingUrl.getHost();
        int port = tilbakemeldingUrl.getPort();

        //TODO: endres til å benytte konfigurasjon fra env-config så fort ressurs for email er på plass (MODTP-700)
        String tilbakemeldingEpost = "minehenvendelser@nav.no";
        return new Epostsender(host, port, APPLICATION_NAME, tilbakemeldingEpost);
    }

    @Bean
    public ContentRetriever enonicContentRetriever() {
        return new EnonicContentRetriever();
    }

    @Bean
    public CmsContentRetriever cmsContentRetriever() throws URISyntaxException {
        CmsContentRetriever cmsContentRetriever = new CmsContentRetriever();
        cmsContentRetriever.setDefaultLocale(DEFAULT_LOCALE);
        cmsContentRetriever.setCmsIp(cmsBaseUrl);
        cmsContentRetriever.setTeksterRetriever(siteContentRetriever());
        cmsContentRetriever.setArtikkelRetriever(siteContentRetriever());
        return cmsContentRetriever;
    }
}
