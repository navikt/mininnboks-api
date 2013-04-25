package no.nav.sbl.dialogarena.minehenvendelser.consumer.util;

import no.nav.modig.content.ValueRetriever;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class CmsContentRetrieverTest {

    private static final String CMS_IP = "10.0.0.1";
    private static final String DEFAULT_LOCALE = "nb";
    private CmsContentRetriever cmsContentRetriever;

    @Before
    public void setup() {
        cmsContentRetriever = new CmsContentRetriever();
        ValueRetriever valueRetriever = Mockito.mock(ValueRetriever.class);
        when(valueRetriever.getValueOf("TEXT", "nb")).thenReturn("Test text");
        cmsContentRetriever.setDefaultLocale(DEFAULT_LOCALE);
        cmsContentRetriever.setArtikkelRetriever(valueRetriever);
        cmsContentRetriever.setTeksterRetriever(valueRetriever);
        cmsContentRetriever.setCmsIp(CMS_IP);
    }

    @Test
    public void getCmsIpShouldReturnCmsIp() {
        assertThat(cmsContentRetriever.getCmsIp(), is(CMS_IP));
    }

    @Test
    public void hentTekstShouldGetCorrectText() {
        assertThat(cmsContentRetriever.hentTekst("TEXT"), equalTo("Test text"));
    }

    @Test
    public void hentArtikkelShouldGetCorrectText() {
        assertThat(cmsContentRetriever.hentArtikkel("TEXT"), equalTo("Test text"));
    }
}
