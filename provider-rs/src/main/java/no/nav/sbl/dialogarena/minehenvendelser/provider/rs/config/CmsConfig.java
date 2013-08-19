package no.nav.sbl.dialogarena.minehenvendelser.provider.rs.config;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.content.ValuesFromContentWithResourceBundleFallback;
import no.nav.modig.content.enonic.HttpContentRetriever;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Produksjonskontekst for webmodulen
 */
@Configuration
public class CmsConfig {

    @Value("${dialogarena.cms.url}")
    private String cmsBaseUrl;

    private static final String DEFAULT_LOCALE = "nb";
    private static final String INNHOLDSTEKSTER_NB_NO_REMOTE = "/site/16/minehenvendelser/nb/tekster";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "content.innholdstekster";
    private static final String SBL_WEBKOMPONENTER_NB_NO_REMOTE = "/site/16/sbl-webkomponenter/nb/tekster";
    private static final String SBL_WEBKOMPONENTER_NB_NO_LOCAL = "content.sbl-webkomponenter";

    @Bean
    public ValueRetriever siteContentRetriever() throws URISyntaxException {
        Map<String, List<URI>> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE, Arrays.asList(new URI(cmsBaseUrl + INNHOLDSTEKSTER_NB_NO_REMOTE), new URI(cmsBaseUrl + SBL_WEBKOMPONENTER_NB_NO_REMOTE)));
        return new ValuesFromContentWithResourceBundleFallback(Arrays.asList(INNHOLDSTEKSTER_NB_NO_LOCAL, SBL_WEBKOMPONENTER_NB_NO_LOCAL), enonicContentRetriever(),
                uris, DEFAULT_LOCALE);
    }

    @Bean(name = "cmsBaseUrl")
    public String cmsBaseUrl() {
        return cmsBaseUrl;
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ContentRetriever enonicContentRetriever() {
        return new HttpContentRetriever();
    }

    @Bean
    public CmsContentRetriever cmsContentRetriever() throws URISyntaxException {
        CmsContentRetriever cmsContentRetriever = new CmsContentRetriever();
        cmsContentRetriever.setDefaultLocale(DEFAULT_LOCALE);
        cmsContentRetriever.setTeksterRetriever(siteContentRetriever());
        cmsContentRetriever.setArtikkelRetriever(siteContentRetriever());
        return cmsContentRetriever;
    }

}
