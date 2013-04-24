package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventning;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg;

public class MockCreationUtil {

    public static Dokumentforventning createMock(boolean isHovedskjema, WSInnsendingsValg innsendingsValg){
        WSDokumentForventning wsDokumentForventning = new WSDokumentForventning()
                .withHovedskjema(isHovedskjema)
                .withInnsendingsValg(innsendingsValg);
        Dokumentforventning dokumentforventning = Dokumentforventning.transformToDokumentforventing(wsDokumentForventning);
        return dokumentforventning;
//        Dokumentforventning dokumentforventning = mock(Dokumentforventning.class);
//        when(dokumentforventning.isHovedskjema()).thenReturn(isHovedskjema);
//        when(dokumentforventning.isLastetOpp()).thenReturn(isLastetOpp);
//        return dokumentforventning;
    }

}
