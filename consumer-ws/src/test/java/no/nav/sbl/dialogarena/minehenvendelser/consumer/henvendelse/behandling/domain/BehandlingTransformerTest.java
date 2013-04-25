package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummeringer;
import org.joda.time.DateTime;
import org.junit.Test;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.transformToBehandling;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBehandlingsstatus.FERDIG;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg.LASTET_OPP;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg.SENDES_IKKE;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class BehandlingTransformerTest {

    @Test
    public void shouldTransformCorrectlyWithBasicFields() {
        DateTime innsendtDato = new DateTime(2013, 01, 01, 01, 01);
        DateTime sistEndret = new DateTime(2013, 01, 02, 01, 01);
        WSBrukerBehandling wsBrukerBehandling = new WSBrukerBehandling()
                .withStatus(FERDIG)
                .withBehandlingsId("behandlingId")
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(innsendtDato)
                .withSistEndret(sistEndret)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer());

        Behandling behandling = transformToBehandling(wsBrukerBehandling);

        assertThat(behandling.getStatus().name(), equalTo(FERDIG.name()));
        assertThat(behandling.getBehandlingsId(), equalTo("behandlingId"));
        assertThat(behandling.getHovedskjemaId(), equalTo("hovedSkjemaId"));
        assertThat(behandling.getInnsendtDato(), equalTo(innsendtDato));
        assertThat(behandling.getSistEndret(), equalTo(sistEndret));
    }

    @Test
    public void shouldTransformCorrectlyWithListOfForventninger() {
        WSDokumentForventningOppsummering wsDokumentForventningOppsummering = new WSDokumentForventningOppsummering()
                .withFriTekst("fritekst1")
                .withHovedskjema(true)
                .withInnsendingsValg(SENDES_IKKE)
                .withKodeverkId("id1");
        WSDokumentForventningOppsummering wsDokumentForventningOppsummering1 = new WSDokumentForventningOppsummering()
                .withFriTekst("fritekst2")
                .withHovedskjema(false)
                .withInnsendingsValg(LASTET_OPP)
                .withKodeverkId("id2");
        WSBrukerBehandling wsBrukerBehandling = MockCreationUtil.createWsBehandlingMock();
        wsBrukerBehandling.withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer().withDokumentForventningOppsummering(wsDokumentForventningOppsummering, wsDokumentForventningOppsummering1));

        Behandling behandling = transformToBehandling(wsBrukerBehandling);

        assertThat(behandling.getDokumentforventninger().size(), equalTo(2));
        assertThat(behandling.getDokumentforventninger().get(0).getFriTekst(), equalTo("fritekst1"));
        assertThat(behandling.getDokumentforventninger().get(1).getFriTekst(), equalTo("fritekst2"));
    }

}
