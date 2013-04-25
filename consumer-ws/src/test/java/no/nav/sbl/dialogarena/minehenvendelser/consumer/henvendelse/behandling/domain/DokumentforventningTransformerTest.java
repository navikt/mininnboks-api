package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventning;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class DokumentforventningTransformerTest {

    @Test
    public void shouldTransformCorrectly() {
        WSDokumentForventningOppsummering wsDokumentForventning = new WSDokumentForventningOppsummering()
                .withFriTekst("fritekst")
                .withHovedskjema(true)
                .withInnsendingsValg(WSInnsendingsValg.SENDES_IKKE)
                .withKodeverkId("kodeverk");
        Dokumentforventning dokumentforventning = Dokumentforventning.transformToDokumentforventing(wsDokumentForventning);

        Assert.assertThat(dokumentforventning.getFriTekst(), equalTo("fritekst"));
        Assert.assertThat(dokumentforventning.getInnsendingsvalg(), equalTo(Dokumentforventning.Innsendingsvalg.SENDES_IKKE));
        Assert.assertThat(dokumentforventning.isHovedskjema(), equalTo(true));
        Assert.assertThat(dokumentforventning.getKodeverkId(),equalTo("kodeverk"));
    }

}
