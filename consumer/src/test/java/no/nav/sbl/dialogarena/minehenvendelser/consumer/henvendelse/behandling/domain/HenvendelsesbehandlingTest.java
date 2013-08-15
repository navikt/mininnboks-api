package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling.Dokumentbehandlingstatus.DOKUMENT_BEHANDLING;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling.Dokumentbehandlingstatus.DOKUMENT_ETTERSENDING;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling.transformToBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createDokumentforventning;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createWsBehandlingMock;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.LASTET_OPP;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.SENDES_IKKE;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

public class HenvendelsesbehandlingTest {

    private static final WSInnsendingsValg IS_INNSENDT = LASTET_OPP;
    private static final WSInnsendingsValg NOT_INNSENDT = SENDES_IKKE;
    private static final boolean IS_HOVEDSKJEMA = true;
    private static final boolean NOT_HOVEDSKJEMA = false;
    public static final String KODEVERK_ID = "kodeverkId";
    private Henvendelsesbehandling henvendelsesbehandling;

    @Before
    public void setup() {
        WSBrukerBehandlingOppsummering wsBrukerBehandling = createWsBehandlingMock();
        henvendelsesbehandling = transformToBehandling(wsBrukerBehandling);
    }

    @Test
    public void filterDokumenterShouldReturnAlleDokumenterWhichAreHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = henvendelsesbehandling.getDokumentforventninger();
        Dokumentforventning dokumentforventning = createDokumentforventning(IS_HOVEDSKJEMA, IS_INNSENDT);
        dokumentforventningList.add(dokumentforventning);
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(henvendelsesbehandling.fetchHoveddokument(), equalTo(dokumentforventning));
    }

    @Test
    public void filterDokumenterShouldReturnAlleDokumenter() {
        List<Dokumentforventning> dokumentforventningList = henvendelsesbehandling.getDokumentforventninger();
        dokumentforventningList.add(createDokumentforventning(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(henvendelsesbehandling.getDokumentforventninger().size(), equalTo(5));
    }

    @Test
    public void shouldCountCorrectAmountOfInnsendteDokumenter() {
        henvendelsesbehandling.getDokumentforventninger().add(createDokumentforventning(NOT_HOVEDSKJEMA, IS_INNSENDT));
        henvendelsesbehandling.getDokumentforventninger().add(createDokumentforventning(NOT_HOVEDSKJEMA, IS_INNSENDT));
        henvendelsesbehandling.getDokumentforventninger().add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        henvendelsesbehandling.getDokumentforventninger().add(createDokumentforventning(IS_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(henvendelsesbehandling.getInnsendteDokumenter(false).size(), is(2));
    }

    @Test
    public void shouldCountCorrectAmountOfDokumenter() {
        henvendelsesbehandling.getDokumentforventninger().add(createDokumentforventning(NOT_HOVEDSKJEMA, IS_INNSENDT));
        henvendelsesbehandling.getDokumentforventninger().add(createDokumentforventning(NOT_HOVEDSKJEMA, IS_INNSENDT));
        henvendelsesbehandling.getDokumentforventninger().add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        henvendelsesbehandling.getDokumentforventninger().add(createDokumentforventning(IS_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(henvendelsesbehandling.getRelevanteDokumenter().size(), is(4));
    }

    @Test
    public void shouldReturnAntallDokumenterUnntattHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = henvendelsesbehandling.getDokumentforventninger();
        Dokumentforventning hovedSkjema = createDokumentforventning(IS_HOVEDSKJEMA, IS_INNSENDT);
        setInternalState(henvendelsesbehandling, "dokumentbehandlingstatus", DOKUMENT_ETTERSENDING);
        dokumentforventningList.add(hovedSkjema);
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(henvendelsesbehandling.getRelevanteDokumenter().size(), equalTo(3));
    }

    @Test
    public void shouldReturnNumberOfMissingDokumenter() {
        List<Dokumentforventning> dokumentforventningList = henvendelsesbehandling.getDokumentforventninger();
        Dokumentforventning hovedSkjema = createDokumentforventning(IS_HOVEDSKJEMA, IS_INNSENDT);
        setInternalState(hovedSkjema, KODEVERK_ID, KODEVERK_ID);
        dokumentforventningList.add(hovedSkjema);
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(henvendelsesbehandling.hasManglendeDokumenter(), equalTo(true));
    }

    @Test
    public void shouldReturnNumberOfMissingDokumenterExceptHoveddokument() {
        List<Dokumentforventning> dokumentforventningList = henvendelsesbehandling.getDokumentforventninger();
        Dokumentforventning hovedSkjema = createDokumentforventning(IS_HOVEDSKJEMA, NOT_INNSENDT);
        dokumentforventningList.add(hovedSkjema);
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(henvendelsesbehandling.getInnsendteDokumenter(true).size(), equalTo(1));
    }

    @Test
    public void getTittelShouldReturnKodeverkIdFromHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = henvendelsesbehandling.getDokumentforventninger();
        Dokumentforventning hovedSkjema = createDokumentforventning(IS_HOVEDSKJEMA, IS_INNSENDT);
        setInternalState(hovedSkjema, KODEVERK_ID, KODEVERK_ID);
        dokumentforventningList.add(hovedSkjema);
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createDokumentforventning(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(henvendelsesbehandling.getHovedskjemaId(), equalTo("hovedSkjemaId-1"));
    }

    @After
    public void resetData() {
        setInternalState(henvendelsesbehandling, "dokumentbehandlingstatus", DOKUMENT_BEHANDLING);
    }

}
