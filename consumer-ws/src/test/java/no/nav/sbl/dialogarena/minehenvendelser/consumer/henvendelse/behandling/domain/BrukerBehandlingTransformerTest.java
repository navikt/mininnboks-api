package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import org.joda.time.DateTime;
import org.junit.Test;

import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandlingType.DOKUMENT_BEHANDLING;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentbehandlingType.SOKNADSINNSENDING;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class BrukerBehandlingTransformerTest {

    @Test
    public void shouldTransformCorrectly() {
        DateTime innsendtDato = new DateTime(2013, 01, 01, 01, 01);
        DateTime sistEndret = new DateTime(2013, 01, 02, 01, 01);
        WSBrukerBehandling wsBrukerBehandling = new WSBrukerBehandling()
                .withBrukerBehandlingType(DOKUMENT_BEHANDLING)
                .withBehandlingsId("behandlingId")
                .withDokumentbehandlingType(SOKNADSINNSENDING)
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(innsendtDato)
                .withSistEndret(sistEndret);

        Behandling behandling = Behandling.transformToBehandling(wsBrukerBehandling);

        assertThat(behandling.getBehandlingsId(), equalTo("behandlingId"));
        assertThat(behandling.getHovedskjemaId(), equalTo("hovedSkjemaId"));
//        assertThat(behandling.getInnsendtDato(), equalTo(innsendtDato));
//        assertThat(behandling.getInnsendtDato(), equalTo(sistEndret));

    }
}
