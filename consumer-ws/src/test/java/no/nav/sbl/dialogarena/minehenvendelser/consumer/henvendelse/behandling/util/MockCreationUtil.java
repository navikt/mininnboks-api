package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBehandlingsstatus;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummering;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSDokumentForventningOppsummeringer;
import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSInnsendingsValg;
import org.joda.time.DateTime;

public class MockCreationUtil {

    public static Dokumentforventning createMock(boolean isHovedskjema, WSInnsendingsValg innsendingsValg) {
        WSDokumentForventningOppsummering wsDokumentForventning = new WSDokumentForventningOppsummering()
                .withHovedskjema(isHovedskjema)
                .withInnsendingsValg(innsendingsValg);
        Dokumentforventning dokumentforventning = Dokumentforventning.transformToDokumentforventing(wsDokumentForventning);
        return dokumentforventning;
    }

    public static WSBrukerBehandling createWsBehandlingMock() {
        DateTime innsendtDato = new DateTime(2013, 01, 01, 01, 01);
        DateTime sistEndret = new DateTime(2013, 01, 02, 01, 01);
        return new WSBrukerBehandling()
                .withStatus(WSBehandlingsstatus.FERDIG)
                .withBehandlingsId("behandlingId")
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(innsendtDato)
                .withSistEndret(sistEndret)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer());
    }

    public static WSBrukerBehandling createWsBehandlingMock(DateTime innsendtDato, DateTime sistEndret, WSBehandlingsstatus status) {
        return new WSBrukerBehandling()
                .withStatus(status)
                .withBehandlingsId("behandlingId")
                .withHovedskjemaId("hovedSkjemaId")
                .withInnsendtDato(innsendtDato)
                .withSistEndret(sistEndret)
                .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer());
    }


    public static WSDokumentForventningOppsummering createDokumentForventningMock(boolean hovedDok, String kodeverkId, WSInnsendingsValg innsendingsValg){
        return new WSDokumentForventningOppsummering().withKodeverkId(kodeverkId).withInnsendingsValg(innsendingsValg).withHovedskjema(hovedDok);
    }

    public static WSBrukerBehandling createFerdigBehandling(){
        WSBrukerBehandling wsBehandlingMock = createWsBehandlingMock(new DateTime(2013,01,01,01,01),new DateTime(2013,01,01,01,01), WSBehandlingsstatus.FERDIG );
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock( true, "1", WSInnsendingsValg.LASTET_OPP),
                createDokumentForventningMock( false, "2", WSInnsendingsValg.LASTET_OPP));
        return wsBehandlingMock;
    }

    public static WSBrukerBehandling createUnderArbeidBehandling(){
        WSBrukerBehandling wsBehandlingMock = createWsBehandlingMock(new DateTime(2013,01,01,01,01),new DateTime(2013,01,01,01,01), WSBehandlingsstatus.UNDER_ARBEID );
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createDokumentForventningMock( true, "1", WSInnsendingsValg.IKKE_VALGT),
                createDokumentForventningMock( false, "2", WSInnsendingsValg.IKKE_VALGT));
        return wsBehandlingMock;
    }



}
