package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.innholdshenter.common.EnonicContentRetriever;
import no.nav.innholdshenter.filter.DecoratorFilter;
import no.nav.modig.content.*;
import no.nav.modig.content.enonic.HttpContentRetriever;
import no.nav.sbl.dialogarena.mininnboks.ResourcesRoot;
import org.apache.commons.io.Charsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Configuration
@EnableScheduling
public class ContentConfig {
    private static final String DEFAULT_LOCALE = "nb";
    private static final String INNHOLDSTEKSTER_NB_NO_REMOTE = "/app/mininnboks/nb/tekster";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "no.nav.sbl.dialogarena.mininnboks.innhold_nb";
    private static final List<String> NO_DECORATOR_PATTERNS = new ArrayList<>(asList(".*/img/.*", ".*selftest.*"));
    private static final Reader PROPERTIES = new InputStreamReader(ResourcesRoot.class.getResourceAsStream("innhold_nb.properties"), Charsets.UTF_8);

    @Value("${appres.cms.url}")
    private String appresUrl;

    @Bean
    public PropertyResolver propertyResolver(CmsContentRetriever contentRetriever) {
        return new PropertyResolver(contentRetriever, PROPERTIES);
    }

    @Bean
    public ValueRetriever siteContentRetriever() throws URISyntaxException {
        Map<String, List<URI>> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE,
                asList(new URI(appresUrl + INNHOLDSTEKSTER_NB_NO_REMOTE)));
        return new ValuesFromContentWithResourceBundleFallback(
                asList(INNHOLDSTEKSTER_NB_NO_LOCAL), enonicContentRetriever(),
                uris, DEFAULT_LOCALE);
    }

    @Bean(name = "appresUrl")
    public String appresUrl() {
        return appresUrl;
    }

    @Bean
    public ContentRetriever enonicContentRetriever() {
        // Egen bønne for å hooke opp @Cacheable
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

    @Bean
    public DecoratorFilter decoratorFilter() {
        DecoratorFilter decoratorFilter = new DecoratorFilter();
        decoratorFilter.setContentRetriever(appresContentRetriever());
        decoratorFilter.setNoDecoratePatterns(NO_DECORATOR_PATTERNS);
        decoratorFilter.setFragmentsUrl("common-html/v1-1/navno");
        decoratorFilter.setApplicationName("Min Innboks");
        decoratorFilter.setFragmentNames(asList(
                "header-withmenu",
                "footer-withmenu",
                "scripts-notrack",
                "styles",
                "webstats-ga",
                "skiplinks"
        ));

        return decoratorFilter;
    }

    private EnonicContentRetriever appresContentRetriever() {
        EnonicContentRetriever contentRetriever = new EnonicContentRetriever("mininnboks");
        contentRetriever.setBaseUrl(appresUrl);
        contentRetriever.setRefreshIntervalSeconds(1800);
        contentRetriever.setHttpTimeoutMillis(10000);
        return contentRetriever;
    }

}
