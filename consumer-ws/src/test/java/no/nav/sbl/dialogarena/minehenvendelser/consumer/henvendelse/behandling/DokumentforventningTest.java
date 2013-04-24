package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

public class DokumentforventningTest {

    private Dokumentforventning dokumentforventning;

    @Before
    public void setup() {
        dokumentforventning = new Dokumentforventning();
    }

    @Test
    public void testIsLastetOpp() throws Exception {
        setInternalState(dokumentforventning, "innsendingsvalg", Dokumentforventning.Innsendingsvalg.LASTET_OPP);
        assertThat(dokumentforventning.isLastetOpp(), is(true));
    }

    @Test
    public void testGetTittel() throws Exception {
        setInternalState(dokumentforventning, "kodeverkId", "kodeverkId");
        assertThat(dokumentforventning.getTittel(), is("KodeverkData kodeverkId"));
    }
}
