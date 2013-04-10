package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.util;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockCreationUtil {

    public static Dokumentforventning createMock(boolean isHovedskjema, boolean isInnsendt){
        Dokumentforventning dokumentforventning = mock(Dokumentforventning.class);
        when(dokumentforventning.isHovedskjema()).thenReturn(isHovedskjema);
        when(dokumentforventning.erInnsendt()).thenReturn(isInnsendt);
        return dokumentforventning;
    }

}
