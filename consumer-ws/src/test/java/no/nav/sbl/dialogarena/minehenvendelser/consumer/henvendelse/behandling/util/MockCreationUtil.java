package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBehandlingsstatus;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventning;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg;
import org.joda.time.DateTime;

import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandlingType.DOKUMENT_BEHANDLING;
import static no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentbehandlingType.SOKNADSINNSENDING;

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

    public static WSBrukerBehandling createWsBehandlingMock() {
        DateTime innsendtDato = new DateTime(2013, 01, 01, 01, 01);
        DateTime sistEndret = new DateTime(2013, 01, 02, 01, 01);
        return new WSBrukerBehandling()
                .withStatus(WSBehandlingsstatus.FERDIG)
                .withBrukerBehandlingType(DOKUMENT_BEHANDLING)
                .withBehandlingsId("behandlingId")
                .withDokumentbehandlingType(SOKNADSINNSENDING)
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(innsendtDato)
                .withSistEndret(sistEndret);
    }

}
