package no.nav.sbl.dialogarena.minehenvendelser.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.content.ValuesFromContentWithResourceBundleFallback;
import no.nav.modig.content.enonic.EnonicContentRetriever;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.CmsContentRetriver;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.Innholdstekster;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebContext {

    private static final String DEFAULT_LOCALE = "nb";
    private static final String INNHOLDSTEKSTER_NB_NO_REMOTE = "/site/16/minehenvendelser/nb/tekster";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "content.innholdstekster";

    @Inject
    private LedeteksterConfiguration ledeteksterConfiguration;

    @Value("${minehenvendelser.cms.url}")
    private String cmsBaseUrl;

    @Bean
    public WicketApplication minehenvendelserApplication() {
        return new WicketApplication();
    }

    @Bean
    public ValueRetriever siteContentRetriever() throws URISyntaxException {
        Map<String, URI> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE, new URI(cmsBaseUrl + INNHOLDSTEKSTER_NB_NO_REMOTE));
        return new ValuesFromContentWithResourceBundleFallback(INNHOLDSTEKSTER_NB_NO_LOCAL, enonicContentRetriever(), uris, DEFAULT_LOCALE);
    }

    @Bean
    public ContentRetriever enonicContentRetriever() {
        return new EnonicContentRetriever();
    }

    @Bean
    public CmsContentRetriver cmsContentRetriver() throws URISyntaxException {
        CmsContentRetriver cmsContentRetriver = new CmsContentRetriver();
        cmsContentRetriver.setCmsIp(cmsBaseUrl);
        cmsContentRetriver.setTeksterRetriver(siteContentRetriever());
        cmsContentRetriver.setArtikkelRetriver(siteContentRetriever());
        return cmsContentRetriver;
    }

    @Bean
    public Innholdstekster innholdsteksterRetriver() throws URISyntaxException {
        Innholdstekster innholdsteksterRetriver = new Innholdstekster();
        innholdsteksterRetriver.setPropertyFileContentRetriver(ledeteksterConfiguration.propertyFileContentRetriver());
        innholdsteksterRetriver.setCmsContentRetriver(cmsContentRetriver());
        innholdsteksterRetriver.setDefaultLocale(DEFAULT_LOCALE);
        return innholdsteksterRetriver;
    }
}
