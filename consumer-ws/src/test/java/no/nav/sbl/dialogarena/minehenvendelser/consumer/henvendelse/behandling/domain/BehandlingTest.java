package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.transformToBehandling;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createMock;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg.LASTET_OPP;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg.SENDES_IKKE;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class BehandlingTest {

    private static final WSInnsendingsValg IS_INNSENDT = LASTET_OPP;
    private static final WSInnsendingsValg NOT_INNSENDT = SENDES_IKKE;
    private static final boolean IS_HOVEDSKJEMA = true;
    private static final boolean NOT_HOVEDSKJEMA = false;

    private Behandling behandling;

    @Before
    public void setup() {
        WSBrukerBehandling wsBrukerBehandling = new WSBrukerBehandling();
        behandling = transformToBehandling(wsBrukerBehandling);
    }

    @Test
    public void shouldCountCorrectAmountOfInnsendteDokumenter() {
        behandling.getDokumentforventninger().add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        behandling.getDokumentforventninger().add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        behandling.getDokumentforventninger().add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        behandling.getDokumentforventninger().add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.getAntallInnsendteDokumenter(), is(2));
    }

    @Test
    public void shouldCountCorrectAmountOfDokumenter() {
        behandling.getDokumentforventninger().add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        behandling.getDokumentforventninger().add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        behandling.getDokumentforventninger().add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        behandling.getDokumentforventninger().add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.getAntallSubDokumenter(), is(3));
    }

    @Test
    public void filterDokumenterShouldReturnAllInsendteDokumenterWhichAreNotHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.fetchInnsendteDokumenter().size(), equalTo(2));
    }

    @Test
    public void filterDokumenterShouldReturnAlleDokumenterWhichAreNotHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.fetchAlleDokumenter().size(), equalTo(3));
    }

    @Test
    public void filterDokumenterShouldReturnAlleDokumenterWhichAreHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        Dokumentforventning dokumentforventning = createMock(IS_HOVEDSKJEMA, IS_INNSENDT);
        dokumentforventningList.add(dokumentforventning);
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.fetchHoveddokument(), equalTo(dokumentforventning));
    }

    @Test
    public void filterDokumenterShouldReturnAlleDokumenter() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.getDokumentforventninger().size(), equalTo(5));
    }

    @Test
    public void getTittelShouldReturnKodeverkIdFromHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        Dokumentforventning hovedSkjema = createMock(IS_HOVEDSKJEMA, IS_INNSENDT);
        Whitebox.setInternalState(hovedSkjema, "kodeverkId", "kodeverkId");
        dokumentforventningList.add(hovedSkjema);
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.getTittel(), equalTo("KodeverkData kodeverkId"));
    }

    @Test
    public void statusTransformerWorks() {
        Whitebox.setInternalState(behandling,"status", Behandling.Behandlingsstatus.FERDIG);
        assertThat(Behandling.STATUS.transform(behandling), equalTo(Behandling.Behandlingsstatus.FERDIG));
    }

}
