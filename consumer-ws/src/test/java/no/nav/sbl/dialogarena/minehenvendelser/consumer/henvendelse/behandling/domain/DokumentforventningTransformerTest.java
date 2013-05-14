package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import org.junit.Test;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.transformToDokumentforventing;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg.SENDES_IKKE;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DokumentforventningTransformerTest {

    @Test
    public void shouldTransformCorrectly() {
        WSDokumentForventningOppsummering wsDokumentForventningOppsummering = new WSDokumentForventningOppsummering()
                .withKodeverkId("kodeverk")
                .withInnsendingsValg(SENDES_IKKE)
                .withHovedskjema(true)
                .withFriTekst("fritekst");
        Dokumentforventning dokumentforventning = transformToDokumentforventing(wsDokumentForventningOppsummering);

        assertThat(dokumentforventning.getKodeverkId(), equalTo("kodeverk"));
        assertThat(dokumentforventning.getInnsendingsvalg(), equalTo(Dokumentforventning.Innsendingsvalg.SENDES_IKKE));
        assertThat(dokumentforventning.isHovedskjema(), equalTo(true));
        assertThat(dokumentforventning.getFriTekst(), equalTo("fritekst"));
    }

}
