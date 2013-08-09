package no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling;

import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokument;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;

public class HenvendelsesBehandlingPortTypeMock implements HenvendelsesBehandlingPortType {
    @Override
    public WSDokument hentDokument(@WebParam(name = "dokumentId", targetNamespace = "") long l) {
        return new WSDokument();
    }

    @Override
    public WSDokumentForventning hentDokumentForventning(@WebParam(name = "dokumentForventingsId", targetNamespace = "") long l) {
        return new WSDokumentForventning();
    }

    @Override
    public List<WSBrukerBehandlingOppsummering> hentBrukerBehandlingListe(@WebParam(name = "aktorId", targetNamespace = "") String s) {
        return new ArrayList<>();
    }

    @Override
    public boolean ping() {
        return true;
    }

    @Override
    public List<WSDokumentForventning> hentDokumentForventningListe(@WebParam(name = "behandlingsId", targetNamespace = "") String s) {
        return new ArrayList<>();
    }

    @Override
    public WSBrukerBehandling hentBrukerBehandling(@WebParam(name = "behandlingsId", targetNamespace = "") String s) {
        return new WSBrukerBehandling();
    }
}
