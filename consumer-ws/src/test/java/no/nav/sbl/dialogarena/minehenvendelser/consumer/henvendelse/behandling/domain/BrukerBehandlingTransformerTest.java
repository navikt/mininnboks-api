package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandlingType;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentbehandlingType;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class BrukerBehandlingTransformerTest {

    @Test
    public void shouldTransformCorrectly() {
        WSBrukerBehandling wsBrukerBehandling = new WSBrukerBehandling()
                .withBrukerBehandlingType(WSBrukerBehandlingType.DOKUMENT_BEHANDLING)
                .withBehandlingsId("behandlingId")
                .withDokumentbehandlingType(WSDokumentbehandlingType.SOKNADSINNSENDING)
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(new DateTime(2013, 01, 01, 01, 01))
                .withSistEndret(new DateTime(2013, 01, 02, 01, 01));

        Behandling behandling = Behandling.transformToBehandling(wsBrukerBehandling);

        assertThat(behandling.getBehandlingsId(), equalTo("behandlingId"));
        assertThat(behandling.getHovedskjemaId(), equalTo("hovedSkjemaId"));

    }
}
