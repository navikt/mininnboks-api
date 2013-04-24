package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventning;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class DokumentforventningTransformerTest {

    @Test
    public void shouldTransformCorrectly() {
        WSDokumentForventning wsDokumentForventning = new WSDokumentForventning()
                .withDokumentId(1L)
                .withFriTekst("fritekst")
                .withHovedskjema(true)
                .withId(2L)
                .withInnsendingsValg(WSInnsendingsValg.SENDES_IKKE)
                .withKodeverkId("kodeverk");
        Dokumentforventning dokumentforventning = Dokumentforventning.transformToDokumentforventing(wsDokumentForventning);

        Assert.assertThat(dokumentforventning.getFriTekst(), equalTo("fritekst"));
    }

}
