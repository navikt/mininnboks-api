package no.nav.sbl.dialogarena.mininnboks.message;


import net.sf.ehcache.CacheManager;
import no.nav.modig.content.Content;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.enonic.innholdstekst.Innholdstekst;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static java.lang.StrictMath.random;
import static java.lang.System.getProperty;
import static java.lang.System.lineSeparator;
import static no.nav.modig.content.enonic.innholdstekst.Innholdstekst.KEY;
import static org.apache.commons.io.FileUtils.write;
import static org.slf4j.LoggerFactory.getLogger;

public class HentNyeTekster {

    protected final Logger logger = getLogger(getClass());

    @Inject
    private NavMessageSource navMessageSource;

    @Inject
    private ContentRetriever contentRetriever;

    /* Eksponseres for å gjOre klassen testbar */
    @Inject
    public CacheManager cacheManager;
    public String cacheName = "cms.content";

    //Hent innholdstekster på nytt hver time
    @Scheduled(cron = "0 * * * * *")
    public void lastInnNyeInnholdstekster() {
        logger.debug("Leser inn innholdstekster fra enonic");
        clearContentCache();
        try {
            saveLocal("enonic/mininnboks_nb_NO.properties", new URI(getProperty("appres.cms.url") + "/app/mininnboks/nb/tekster?" + random()));
        } catch (Exception e) {
            logger.warn("Feilet under henting av enonic innholdstekster: " + e, e);
        }
        navMessageSource.clearCache();
    }

    private void clearContentCache() {
        cacheManager.getCache(cacheName).flush();
    }

    private void saveLocal(String filename, URI uri) throws IOException {
        File file = new File(getProperty("mininnboks.datadir"), filename);
        logger.debug("Leser inn innholdstekster fra " + uri + " til: " + file.toString());
        Content<Innholdstekst> content = contentRetriever.getContent(uri);
        StringBuilder data = new StringBuilder();
        Map<String, Innholdstekst> innhold = content.toMap(KEY);
        if (!innhold.isEmpty()) {
            for (Map.Entry<String, Innholdstekst> entry : innhold.entrySet()) {
                data.append(entry.getValue().key).append('=').append(removeNewline(entry.getValue().value)).append(lineSeparator());
            }
            write(file, data, "UTF-8");
        }
    }

    private String removeNewline(String value) {
        return value.replaceAll("\n", "");
    }
}