package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventningOppsummeringer;
import org.joda.time.DateTime;
import org.junit.Test;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling.transformToBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createWsBehandlingMock;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBehandlingsstatus.FERDIG;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingType.DOKUMENT_BEHANDLING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentbehandlingType.SOKNADSINNSENDING;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.LASTET_OPP;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.SENDES_IKKE;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class BehandlingTransformerTest {

    @Test
    public void shouldTransformCorrectlyWithBasicFields() {
        DateTime innsendtDato = new DateTime(2013, 01, 01, 01, 01);
        DateTime sistEndret = new DateTime(2013, 01, 02, 01, 01);
        WSBrukerBehandlingOppsummering wsBrukerBehandling = new WSBrukerBehandlingOppsummering()
                .withStatus(FERDIG)
                .withBehandlingsId("behandlingId")
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(innsendtDato)
                .withSistEndret(sistEndret)
                .withBrukerBehandlingType(DOKUMENT_BEHANDLING)
                .withDokumentbehandlingType(SOKNADSINNSENDING)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer());

        Henvendelsesbehandling henvendelsesbehandling = transformToBehandling(wsBrukerBehandling);

        assertThat(henvendelsesbehandling.getBehandlingsstatus().name(), equalTo(FERDIG.name()));
        assertThat(henvendelsesbehandling.getBehandlingsId(), equalTo("behandlingId"));
        assertThat(henvendelsesbehandling.getHovedskjemaId(), equalTo("hovedSkjemaId"));
        assertThat(henvendelsesbehandling.getInnsendtDato(), equalTo(innsendtDato));
        assertThat(henvendelsesbehandling.getSistEndret(), equalTo(sistEndret));
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
        WSBrukerBehandlingOppsummering wsBrukerBehandling = createWsBehandlingMock();
        wsBrukerBehandling.withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer().withDokumentForventningOppsummering(wsDokumentForventningOppsummering, wsDokumentForventningOppsummering1));

        Henvendelsesbehandling henvendelsesbehandling = transformToBehandling(wsBrukerBehandling);

        assertThat(henvendelsesbehandling.getDokumentforventninger().size(), equalTo(2));
        assertThat(henvendelsesbehandling.getDokumentforventninger().get(0).getFriTekst(), equalTo("fritekst1"));
        assertThat(henvendelsesbehandling.getDokumentforventninger().get(1).getFriTekst(), equalTo("fritekst2"));
    }

}
