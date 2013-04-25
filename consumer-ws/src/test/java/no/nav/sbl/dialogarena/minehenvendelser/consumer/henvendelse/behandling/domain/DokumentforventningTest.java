package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain;

import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg;
import org.junit.Before;
import org.junit.Test;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning.Innsendingsvalg.LASTET_OPP;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.internal.util.reflection.Whitebox.setInternalState;

public class DokumentforventningTest {

    private Dokumentforventning dokumentforventning;

    @Before
    public void setup() {
        WSDokumentForventningOppsummering wsDokumentForventningOppsummering = new WSDokumentForventningOppsummering()
                .withInnsendingsValg(WSInnsendingsValg.LASTET_OPP);
        dokumentforventning = Dokumentforventning.transformToDokumentforventing(wsDokumentForventningOppsummering);
    }

    @Test
    public void testIsLastetOpp() throws Exception {
        setInternalState(dokumentforventning, "innsendingsvalg", LASTET_OPP);
        assertThat(dokumentforventning.isLastetOpp(), is(true));
    }

    @Test
    public void testGetTittel() throws Exception {
        setInternalState(dokumentforventning, "kodeverkId", "kodeverkId");
        assertThat(dokumentforventning.getTittel(), is("KodeverkData kodeverkId"));
    }
}
