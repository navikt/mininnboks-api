package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.innholdshenter.common.EnonicContentRetriever;
import no.nav.innholdshenter.filter.DecoratorFilter;
import no.nav.modig.cache.CacheConfig;
import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.ValueRetriever;
import no.nav.modig.content.ValuesFromContentWithResourceBundleFallback;
import no.nav.modig.content.enonic.HttpContentRetriever;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils;
import no.nav.sbl.dialogarena.mininnboks.message.HentNyeTekster;
import no.nav.sbl.dialogarena.mininnboks.message.NavMessageSource;
import no.nav.sbl.dialogarena.types.Pingable;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.getProperty;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;
import static org.slf4j.LoggerFactory.getLogger;

@Configuration
@EnableScheduling
@Import({
        CacheConfig.class
})
public class ContentConfig {

    @Value("${mininnboks.datadir}")
    private File dataDirectory;

    private final Logger logger = getLogger(getClass());

    private static final String DEFAULT_LOCALE = "nb_NO";
    private static final String ENGELSK_LOCALE = "en_GB";
    private static final String INNHOLDSTEKSTER_NB_NO_REMOTE = "/app/mininnboks/nb_NO/tekster";
    private static final String INNHOLDSTEKSTER_EN_GB_REMOTE = "/app/mininnboks/en_GB/tekster";
    private static final String INNHOLDSTEKSTER_NB_NO_LOCAL = "content.mininnboks";
    private static final String FRAGMENTS_URL = "common-html/v3/navno";
    private static final List<String> NO_DECORATOR_PATTERNS = new ArrayList<>(asList(".*/img/.*", ".*selftest.*"));

    @Bean
    public ValueRetriever siteContentRetriever() throws URISyntaxException {
        Map<String, List<URI>> uris = new HashMap<>();
        uris.put(DEFAULT_LOCALE, asList(new URI(getProperty("appres.cms.url") + INNHOLDSTEKSTER_NB_NO_REMOTE)));
        uris.put(ENGELSK_LOCALE, asList(new URI(getProperty("appres.cms.url") + INNHOLDSTEKSTER_EN_GB_REMOTE)));
        return new ValuesFromContentWithResourceBundleFallback(asList(INNHOLDSTEKSTER_NB_NO_LOCAL), contentRetriever(), uris, DEFAULT_LOCALE);
    }

    @Bean
    public CmsContentRetriever cmsContentRetriever() throws URISyntaxException {
        CmsContentRetriever cmsContentRetriever = new CmsContentRetriever();
        cmsContentRetriever.setDefaultLocale(DEFAULT_LOCALE);
        cmsContentRetriever.setTeksterRetriever(siteContentRetriever());
        return cmsContentRetriever;
    }

    @Bean
    public DecoratorFilter decoratorFilter() {
        DecoratorFilter decorator = new DecoratorFilter();
        decorator.setFragmentsUrl(FRAGMENTS_URL);
        decorator.setContentRetriever(appresContentRetriever());
        decorator.setApplicationName("Min innboks");
        decorator.setNoDecoratePatterns(NO_DECORATOR_PATTERNS);
        decorator.setFragmentNames(asList(
                "header-withmenu",
                "footer-withmenu",
                "styles",
                "scripts",
                "webstats-ga",
                "skiplinks"
        ));
        return decorator;
    }

    @Bean
    public ContentRetriever contentRetriever() {
        HttpContentRetriever httpContentRetriever = new HttpContentRetriever();
        httpContentRetriever.http.setTimeout(20 * 1000);
        return httpContentRetriever;
    }

    @Bean
    public NavMessageSource navMessageSource() {
        //Vi lager en reloadablemessagesource som henter både fra lokal disk og fra classpath. Se lastInnNyeInnholdstekster for å se koden som skriver de filene som hentes fra enonic.
        NavMessageSource messageSource = new NavMessageSource();
        String brukerprofilDataDirectoryString = dataDirectory.toURI().toString();

        messageSource.setBasenames(
                new NavMessageSource.Bundle("mininnboks", brukerprofilDataDirectoryString + "enonic/mininnboks", "classpath:content/mininnboks")
        );

        //Sjekk for nye filer en gang hvert 30. minutt.
        messageSource.setCacheSeconds(60 * 30);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public HentNyeTekster hentNyeTekster() {
        return new HentNyeTekster();
    }

    @Bean
    public Pingable cmsPing() {
        return () -> {
            String url = "";
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(getProperty("appres.cms.url")).openConnection();
                connection.setConnectTimeout(10000);
                if (connection.getResponseCode() == HTTP_OK) {
                    return lyktes("APPRES_CMS");
                } else {
                    throw new ApplicationException("Fikk feilkode fra CMS: " + connection.getResponseCode() + ": " + connection.getResponseMessage());
                }
            } catch (Exception e) {
                logger.warn("CMS not reachable on " + url, e);
                return feilet("APPRES_CMS", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        };
    }

    // For aa kunne bruke bean i static context
    @PostConstruct
    private void init() throws URISyntaxException {
        HenvendelsesUtils.setCmsContentRetriever(cmsContentRetriever());
    }

    private EnonicContentRetriever appresContentRetriever() {
        EnonicContentRetriever contentRetriever = new EnonicContentRetriever("mininnboks");
        contentRetriever.setBaseUrl(getProperty("appres.cms.url"));
        contentRetriever.setRefreshIntervalSeconds(1800);
        contentRetriever.setHttpTimeoutMillis(10000);
        return contentRetriever;
    }
}