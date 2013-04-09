package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BehandlingTest {

    private static final boolean NOT_HOVEDSKJEMA = false;
    private static final boolean IS_INNSENDT = true;
    private static final boolean NOT_INNSENDT = false;
    private static final boolean IS_HOVEDSKJEMA = true;

    @Test
    public void shouldCountCorrectAmountOfAttachments(){
        Behandling behandling = new Behandling();

        behandling.getDokumentforventninger().add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        behandling.getDokumentforventninger().add(createMock(NOT_HOVEDSKJEMA, IS_INNSENDT));
        behandling.getDokumentforventninger().add(createMock(NOT_HOVEDSKJEMA, NOT_INNSENDT));
        behandling.getDokumentforventninger().add(createMock(IS_HOVEDSKJEMA, NOT_INNSENDT));

        assertThat(behandling.getAntallInnsendteDokumenter(),is(2));
        assertThat(behandling.getAntallSubDokumenter(),is(3));
    }

    private Dokumentforventning createMock(boolean isHovedskjema, boolean isInnsendt){
        Dokumentforventning dokumentforventning = mock(Dokumentforventning.class);
        when(dokumentforventning.isHovedskjema()).thenReturn(isHovedskjema);
        when(dokumentforventning.erInnsendt()).thenReturn(isInnsendt);
        return dokumentforventning;
    }

}
