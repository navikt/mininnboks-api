package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandlingType;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentbehandlingType;
import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class BrukerBehandlingTransformerTest {

    @Test
    public void shouldTransformCorrectly(){
        WSBrukerBehandling wsBrukerBehandling =  new WSBrukerBehandling()
                .withBrukerBehandlingType(WSBrukerBehandlingType.DOKUMENT_BEHANDLING)
                .withBehandlingsId("behandlingId")
                .withDokumentbehandlingType(WSDokumentbehandlingType.SOKNADSINNSENDING)
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(new DateTime(2013,01,01,01,01))
                .withSistEndret(new DateTime(2013,01,02,01,01));

        Behandling behandling = Behandling.transformToBehandling(wsBrukerBehandling);

        Assert.assertThat(behandling.getBehandlingsId(), equalTo("behandlingId"));
        Assert.assertThat(behandling.getHovedskjemaId(), equalTo("hovedSkjemaId"));

    }
}
