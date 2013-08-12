package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokument;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;

import java.util.ArrayList;
import java.util.List;

public class HenvendelsesBehandlingPortTypeMock implements HenvendelsesBehandlingPortType {

    @Override
    public WSDokument hentDokument(long l) {
        return new WSDokument();
    }

    @Override
    public WSDokumentForventning hentDokumentForventning(long l) {
        return  new WSDokumentForventning();
    }

    @Override
    public List<WSBrukerBehandlingOppsummering> hentBrukerBehandlingListe(String s) {
        return MockCreationUtil.createBrukerBehandlingOppsumeringList();
    }

    @Override
    public boolean ping() {
        return true;
    }

    @Override
    public List<WSDokumentForventning> hentDokumentForventningListe(String s) {
        return new ArrayList<>();
    }

    @Override
    public WSBrukerBehandling hentBrukerBehandling(String s) {
        return new WSBrukerBehandling();
    }
}
