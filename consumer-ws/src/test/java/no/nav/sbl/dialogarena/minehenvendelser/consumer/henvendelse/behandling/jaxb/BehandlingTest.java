package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BehandlingTest {

    @Before
    public void setUp() throws Exception {


    }

    @Test
    public void shouldCountCorrectAmountOfAttachments(){
        Behandling behandling = new Behandling();
        Dokumentforventning dokumentforventning1 = mock(Dokumentforventning.class);
        Dokumentforventning dokumentforventning2 = mock(Dokumentforventning.class);
        Dokumentforventning dokumentforventning3 = mock(Dokumentforventning.class);
        Dokumentforventning dokumentforventning4 = mock(Dokumentforventning.class);
        when(dokumentforventning1.isHovedskjema()).thenReturn(false);
        when(dokumentforventning1.erInnsendt()).thenReturn(true);
        when(dokumentforventning2.isHovedskjema()).thenReturn(false);
        when(dokumentforventning2.erInnsendt()).thenReturn(true);
        when(dokumentforventning3.isHovedskjema()).thenReturn(false);
        when(dokumentforventning3.erInnsendt()).thenReturn(false);
        when(dokumentforventning4.isHovedskjema()).thenReturn(true);
        when(dokumentforventning4.erInnsendt()).thenReturn(false);

        behandling.getDokumentforventninger().add(dokumentforventning1);
        behandling.getDokumentforventninger().add(dokumentforventning2);
        behandling.getDokumentforventninger().add(dokumentforventning3);
        behandling.getDokumentforventninger().add(dokumentforventning4);

        assertThat(behandling.getAntallInnsendteDokumenter(),is(2));
        assertThat(behandling.getAntallSubDokumenter(),is(3));
    }
}
