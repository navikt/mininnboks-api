package no.nav.sbl.dialogarena.minehenvendelser.utils;

import org.junit.Test;

import static no.nav.sbl.dialogarena.minehenvendelser.utils.URLParsingMultiLineLabel.urlToLinkTags;
import static org.junit.Assert.assertEquals;

public class URLParsingMultiLineLabelTest {

    @Test
    public void testUrlToLinkTags() throws Exception {
        assertEquals(urlToLinkTags("http://www.test.no"), "<a href=\"http://www.test.no\">http://www.test.no</a>");
        assertEquals(urlToLinkTags("https://www.test.no"), "<a href=\"https://www.test.no\">https://www.test.no</a>");
        assertEquals(urlToLinkTags("www.test.no"), "<a href=\"www.test.no\">www.test.no</a>");
        assertEquals(urlToLinkTags("test.no"), "<a href=\"test.no\">test.no</a>");

        assertEquals(urlToLinkTags("test. no"), "test. no");
        assertEquals(urlToLinkTags("www..test.no"), "www..<a href=\"test.no\">test.no</a>");
    }
}
