package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling.IS_HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling.IS_INNSENDT;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling.NOT_HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling.NOT_INNSENDT;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.util.MockCreationUtil.createMock;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class BehandlingTest {

    private Behandling behandling;

    @Before
    public void setup() {
        behandling = new Behandling();
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

        assertThat(behandling.filterDokumenter(IS_INNSENDT, NOT_HOVEDSKJEMA).size(), equalTo(2));
    }

    @Test
    public void filterDokumenterShouldReturnAllIkkeInsendteDokumenterWhichAreNotHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.filterDokumenter(NOT_INNSENDT, NOT_HOVEDSKJEMA).size(), equalTo(2));
    }

    @Test
    public void filterDokumenterShouldReturnAlleDokumenterWhichAreNotHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.filterDokumenter(null, NOT_HOVEDSKJEMA).size(), equalTo(3));
    }

    @Test
    public void filterDokumenterShouldReturnAlleDokumenterWhichAreHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.filterDokumenter(null, IS_HOVEDSKJEMA).size(), equalTo(2));
    }

    @Test
    public void filterDokumenterShouldReturnAlleDokumenterWhichAreInnsendt() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.filterDokumenter(IS_INNSENDT, null).size(), equalTo(2));
    }

    @Test
    public void filterDokumenterShouldReturnAlleDokumenterWhichAreInnsendtAndHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.filterDokumenter(IS_INNSENDT, IS_HOVEDSKJEMA).size(), equalTo(1));
    }


    @Test
    public void filterDokumenterShouldReturnAlleDokumenterWhichAreNotInnsendtAndHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.filterDokumenter(NOT_INNSENDT, IS_HOVEDSKJEMA).size(), equalTo(1));
    }


    @Test
    public void filterDokumenterShouldReturnAlleDokumenterWhichAreNotInnsendt() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.filterDokumenter(NOT_INNSENDT, null).size(), equalTo(3));
    }

    @Test
    public void filterDokumenterShouldReturnAlleDokumenter() {
        List<Dokumentforventning> dokumentforventningList = behandling.getDokumentforventninger();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.filterDokumenter(null, null).size(), equalTo(5));
    }
}
