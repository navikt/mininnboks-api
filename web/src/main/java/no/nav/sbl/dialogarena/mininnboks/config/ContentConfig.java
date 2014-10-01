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
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Configuration
public class ContentConfig {

    private static final String DEFAULT_LOCALE = "nb";
    private static final String INNHOLDSTEKSTER_NB_NO_REMOTE = "/app/mininnboks/nb/tekster";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "content.innhold_nb";
    private static final List<String> NO_DECORATOR_PATTERNS = new ArrayList<>(asList(".*/img/.*", ".*selftest.*"));

    @Bean
    public ValueRetriever siteContentRetriever(ContentRetriever contentRetriever) throws URISyntaxException {
        String cmsBaseUrl = System.getProperty("appres.cms.url");
        Map<String, List<URI>> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE,
                asList(new URI(cmsBaseUrl + INNHOLDSTEKSTER_NB_NO_REMOTE)));
        return new ValuesFromContentWithResourceBundleFallback(
                asList(INNHOLDSTEKSTER_NB_NO_LOCAL), contentRetriever,
                uris, DEFAULT_LOCALE);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public ContentRetriever enonicContentRetriever() {
        // Egen bønne for å hooke opp @Cacheable
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
        DecoratorFilter decoratorFilter = new DecoratorFilter();
        decoratorFilter.setContentRetriever(appresContentRetriever());
        decoratorFilter.setNoDecoratePatterns(NO_DECORATOR_PATTERNS);
        decoratorFilter.setFragmentsUrl("common-html/v1/navno");
        decoratorFilter.setApplicationName("Min Innboks");
        decoratorFilter.setFragmentNames(asList(
                "header-withmenu",
                "footer-withmenu",
                "inline-js-variables"
        ));

        return decoratorFilter;
    }

    private EnonicContentRetriever appresContentRetriever() {
        EnonicContentRetriever contentRetriever = new EnonicContentRetriever("mininnboks");
        contentRetriever.setBaseUrl(System.getProperty("appres.cms.url"));
        contentRetriever.setRefreshIntervalSeconds(1800);
        contentRetriever.setHttpTimeoutMillis(10000);
        return contentRetriever;
    }

}
