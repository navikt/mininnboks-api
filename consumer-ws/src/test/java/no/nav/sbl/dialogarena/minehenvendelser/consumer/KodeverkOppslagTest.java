package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class KodeverkOppslagTest {
    private KodeverkOppslag kodeverkOppslag;

    @Before
    public void setup() {
        kodeverkOppslag = new KodeverkOppslag();
        kodeverkOppslag.insertKodeverk("key", "value");
    }

    @Test
    public void shouldGetKodeverk() {
        Assert.assertThat(kodeverkOppslag.hentKodeverk("key"), equalTo("value"));
    }

    @Test
    public void missingKodeverkShouldReport() {
        Assert.assertThat(kodeverkOppslag.hentKodeverk("missingKey"), equalTo("Kodeverk mangler: missingKey"));
    }
}
