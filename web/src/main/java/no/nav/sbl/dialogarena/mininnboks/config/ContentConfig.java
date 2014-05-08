package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.content.ValuesFromContentWithResourceBundleFallback;
import no.nav.modig.content.enonic.HttpContentRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ContentConfig {

    private static final String DEFAULT_LOCALE = "nb";
    private static final String INNHOLDSTEKSTER_NB_NO_REMOTE = "/systemsider/Modernisering/minehenvendelser/nb/tekster";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "content.innhold_nb";
    private static final String SBL_WEBKOMPONENTER_NB_NO_REMOTE = "/systemsider/Modernisering/sbl-webkomponenter/nb/tekster";
    private static final String SBL_WEBKOMPONENTER_NB_NO_LOCAL = "content.sbl-webkomponenter_nb";

    @Bean
    public ValueRetriever siteContentRetriever(ContentRetriever contentRetriever) throws URISyntaxException {
        String cmsBaseUrl = System.getProperty("dialogarena.cms.url");
        Map<String, List<URI>> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE,
                Arrays.asList(
                        new URI(cmsBaseUrl + INNHOLDSTEKSTER_NB_NO_REMOTE),
                        new URI(cmsBaseUrl + SBL_WEBKOMPONENTER_NB_NO_REMOTE)

                ));
        return new ValuesFromContentWithResourceBundleFallback(
                Arrays.asList(INNHOLDSTEKSTER_NB_NO_LOCAL, SBL_WEBKOMPONENTER_NB_NO_LOCAL), contentRetriever,
                uris, DEFAULT_LOCALE);
    }

    @Bean
    public ContentRetriever enonicContentRetriever() {
        // Egen bønne for å hooke opp @Cachable
        return new HttpContentRetriever();
    }

    @Bean
    public CmsContentRetriever cmsContentRetriever(ValueRetriever valueRetriever) throws URISyntaxException {
        CmsContentRetriever cmsContentRetriever = new CmsContentRetriever();
        cmsContentRetriever.setDefaultLocale(DEFAULT_LOCALE);
        cmsContentRetriever.setTeksterRetriever(valueRetriever);
        cmsContentRetriever.setArtikkelRetriever(valueRetriever);
        return cmsContentRetriever;
    }
}
