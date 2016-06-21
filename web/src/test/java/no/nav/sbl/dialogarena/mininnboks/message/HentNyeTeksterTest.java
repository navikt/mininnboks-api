package no.nav.sbl.dialogarena.mininnboks.message;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import no.nav.modig.content.Content;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.content.enonic.innholdstekst.Innholdstekst;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HentNyeTeksterTest {

    @Mock
    private ContentRetriever enonicContentRetriever;

    @Mock
    private NavMessageSource navMessageSource;

    @InjectMocks
    private HentNyeTekster hentNyeTekster;

    static final String TEST_CACHE_ID = "test.id";

    @Test
    public void hentUtTeksterOgLagreTilDisk(){

        System.setProperty("cachetid.sekunder", "10");
        System.setProperty("mininnboks.datadir", System.getProperty("user.dir"));

        CacheManager cacheManager = CacheManager.create();
        Cache cache = new Cache(new CacheConfiguration(TEST_CACHE_ID, 1000));
        cacheManager.addCache(cache);
        hentNyeTekster.cacheManager = cacheManager;
        hentNyeTekster.cacheName = TEST_CACHE_ID;

        when(enonicContentRetriever.getContent(any())).thenReturn(new Content<>(asList(new Innholdstekst("123", "456")), 1));
        Mockito.doNothing().when(navMessageSource).clearCache();

        hentNyeTekster.lastInnNyeInnholdstekster();

        System.clearProperty("mininnboks.datadir");
        System.clearProperty("cachetid.sekunder");
    }

}