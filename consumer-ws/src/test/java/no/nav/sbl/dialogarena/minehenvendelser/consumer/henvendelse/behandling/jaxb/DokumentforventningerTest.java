package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventninger.IS_HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventninger.IS_INNSENDT;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventninger.NOT_HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventninger.NOT_INNSENDT;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.util.MockCreationUtil.createMock;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class DokumentforventningerTest {

    @Test
    public void getInnsendtedokumenterShouldReturnAllInsendteDokumenterWhichAreNotHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = new ArrayList<>();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(Dokumentforventninger.filterDokumenter(dokumentforventningList, IS_INNSENDT, NOT_HOVEDSKJEMA).size(), equalTo(2));
    }

    @Test
    public void getIkkeInnsendtedokumenterShouldReturnAllIkkeInsendteDokumenterWhichAreNotHovedskjema() {
        List<Dokumentforventning> dokumentforventningList = new ArrayList<>();
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        dokumentforventningList.add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(Dokumentforventninger.filterDokumenter(dokumentforventningList, NOT_INNSENDT, NOT_HOVEDSKJEMA).size(), equalTo(2));
    }
}
