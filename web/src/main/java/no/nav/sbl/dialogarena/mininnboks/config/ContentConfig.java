package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.innholdshenter.common.SimpleEnonicClient;
import no.nav.innholdshenter.filter.DecoratorFilter;
import no.nav.modig.cache.CacheConfig;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstService;
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils;
import no.nav.sbl.dialogarena.types.Pingable;
import no.nav.sbl.tekster.TeksterAPI;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;

import javax.inject.Inject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.feilet;
import static no.nav.sbl.dialogarena.types.Pingable.Ping.lyktes;
import static org.slf4j.LoggerFactory.getLogger;

@Configuration
@Import({CacheConfig.class})
public class ContentConfig {
    private static final Logger logger = getLogger(ContentConfig.class);
    private static final String FRAGMENTS_URL = "common-html/v3/navno";
    private static final List<String> NO_DECORATOR_PATTERNS = new ArrayList<>(asList(".*/img/.*", ".*selftest.*"));
    private static final List<String> FRAGMENT_NAMES = asList("header-withmenu", "footer-withmenu", "styles", "scripts", "webstats-ga", "skiplinks");
    private static final String APPLICATION_NAME = "Min innboks";

    @Inject
    ResourceLoader loader;

    @Value("${appres.cms.url}")
    private String appresBaseUrl;

    @Bean
    public SimpleEnonicClient dekoratorClient() {
        return new SimpleEnonicClient(appresBaseUrl);
    }

    @Bean
    public TekstService tekstService() throws IOException {
        String ledeteksterPath = loader.getResource("classpath:tekster").getFile().getPath();
        TeksterAPI teksterAPI = new TeksterAPI(ledeteksterPath, "mininnboks");
        TekstService teksterService = new TekstService.Default(teksterAPI);
        HenvendelsesUtils.setTekstService(teksterService);
        return teksterService;
    }

    @Bean
    public DecoratorFilter decoratorFilter(SimpleEnonicClient dekoratorClient) {
        DecoratorFilter decorator = new DecoratorFilter();
        decorator.setFragmentsUrl(FRAGMENTS_URL);
        decorator.setContentRetriever(dekoratorClient);
        decorator.setApplicationName(APPLICATION_NAME);
        decorator.setNoDecoratePatterns(NO_DECORATOR_PATTERNS);
        decorator.setFragmentNames(FRAGMENT_NAMES);
        return decorator;
    }

    @Bean
    public Pingable cmsPing() {
        return () -> {
            String url = "";
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL(appresBaseUrl).openConnection();
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
}