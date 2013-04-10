package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb;

import org.junit.Test;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.util.MockCreationUtil.IS_HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.util.MockCreationUtil.IS_INNSENDT;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.util.MockCreationUtil.NOT_HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.util.MockCreationUtil.NOT_INNSENDT;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.util.MockCreationUtil.createMock;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BehandlingTest {

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

}
