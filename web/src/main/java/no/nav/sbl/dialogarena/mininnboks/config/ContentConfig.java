package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.innholdshenter.common.EnonicContentRetriever;
import no.nav.innholdshenter.filter.DecoratorFilter;
import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.content.ValuesFromContentWithResourceBundleFallback;
import no.nav.modig.content.enonic.HttpContentRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Configuration
public class ContentConfig {

    private static final String DEFAULT_LOCALE = "nb";
    private static final String INNHOLDSTEKSTER_NB_NO_REMOTE = "/systemsider/Modernisering/minehenvendelser/nb/tekster";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "content.innhold_nb";

    @Bean
    public ValueRetriever siteContentRetriever(ContentRetriever contentRetriever) throws URISyntaxException {
        String cmsBaseUrl = System.getProperty("dialogarena.cms.url");
        Map<String, List<URI>> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE,
                asList(new URI(cmsBaseUrl + INNHOLDSTEKSTER_NB_NO_REMOTE)));
        return new ValuesFromContentWithResourceBundleFallback(
                asList(INNHOLDSTEKSTER_NB_NO_LOCAL), contentRetriever,
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

    @Bean
    public DecoratorFilter decoratorFilter() {
        EnonicContentRetriever enonicContentRetriever = new EnonicContentRetriever("mininnboks");
        enonicContentRetriever.setBaseUrl("https://appres-t1.nav.no");
        enonicContentRetriever.setHttpTimeoutMillis(5000);
        enonicContentRetriever.setRefreshIntervalSeconds(1800);

        DecoratorFilter decoratorFilter = new DecoratorFilter();
        decoratorFilter.setFragmentsUrl("common-html/v1/navno");
        decoratorFilter.setContentRetriever(enonicContentRetriever);
        decoratorFilter.setApplicationName("Min Innboks");
        decoratorFilter.setFragmentNames(asList("resources","header", "footer-withmenu"));

        return decoratorFilter;
    }
}
