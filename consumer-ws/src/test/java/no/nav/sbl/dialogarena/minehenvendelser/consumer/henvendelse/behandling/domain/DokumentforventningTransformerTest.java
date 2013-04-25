package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import org.junit.Assert;
import org.junit.Test;

import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg.SENDES_IKKE;
import static org.hamcrest.Matchers.equalTo;

public class DokumentforventningTransformerTest {

    @Test
    public void shouldTransformCorrectly() {
        WSDokumentForventningOppsummering wsDokumentForventningOppsummering = new WSDokumentForventningOppsummering()
                .withKodeverkId("kodeverk")
                .withInnsendingsValg(SENDES_IKKE)
                .withHovedskjema(true)
                .withFriTekst("fritekst");
        Dokumentforventning dokumentforventning = Dokumentforventning.transformToDokumentforventing(wsDokumentForventningOppsummering);

        Assert.assertThat(dokumentforventning.getKodeverkId(),equalTo("kodeverk"));
        Assert.assertThat(dokumentforventning.getInnsendingsvalg(), equalTo(Dokumentforventning.Innsendingsvalg.SENDES_IKKE));
        Assert.assertThat(dokumentforventning.isHovedskjema(), equalTo(true));
        Assert.assertThat(dokumentforventning.getFriTekst(), equalTo("fritekst"));
    }

}
