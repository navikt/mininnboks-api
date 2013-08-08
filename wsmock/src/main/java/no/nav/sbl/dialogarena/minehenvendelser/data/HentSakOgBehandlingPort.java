package no.nav.sbl.dialogarena.minehenvendelser.data;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

public class HentSakOgBehandlingPort implements SakOgBehandlingPortType {

    private final MockSakOgBehandlingData mockData;

    public HentSakOgBehandlingPort() {
        mockData = new MockSakOgBehandlingData();
    }

    @Override
    @WebResult(name = "response", targetNamespace = "")
    @RequestWrapper(localName = "finnSakOgBehandlingskjedeListe", targetNamespace = "http://nav.no/tjeneste/domene/brukerdialog/henvendelsesbehandling/v1/", className = "no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListe")
    @WebMethod
    @ResponseWrapper(localName = "finnSakOgBehandlingskjedeListeResponse", targetNamespace = "http://nav.no/tjeneste/domene/brukerdialog/henvendelsesbehandling/v1/", className = "no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse")
    public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListe(@WebParam(name = "request", targetNamespace = "") FinnSakOgBehandlingskjedeListeRequest finnSakOgBehandlingskjedeListeRequest) {
        return mockData.finnSakOgBehandlingskjedeListeResponse();
    }

    @Override
    @WebResult(name = "response", targetNamespace = "")
    @RequestWrapper(localName = "hentBehandlingskjedensBehandlinger", targetNamespace = "http://nav.no/tjeneste/domene/brukerdialog/henvendelsesbehandling/v1/", className = "no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlinger")
    @WebMethod
    @ResponseWrapper(localName = "hentBehandlingskjedensBehandlingerResponse", targetNamespace = "http://nav.no/tjeneste/domene/brukerdialog/henvendelsesbehandling/v1/", className = "no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse")
    public HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlinger(@WebParam(name = "request", targetNamespace = "") HentBehandlingskjedensBehandlingerRequest hentBehandlingskjedensBehandlingerRequest) {
        return mockData.hentBehandlingskjedensBehandlingerResponse();
    }

    @Override
    @WebResult(name = "response", targetNamespace = "")
    @RequestWrapper(localName = "hentBehandling", targetNamespace = "http://nav.no/tjeneste/domene/brukerdialog/henvendelsesbehandling/v1/", className = "no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandling")
    @WebMethod
    @ResponseWrapper(localName = "hentBehandlingResponse", targetNamespace = "http://nav.no/tjeneste/domene/brukerdialog/henvendelsesbehandling/v1/", className = "no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingResponse")
    public HentBehandlingResponse hentBehandling(@WebParam(name = "request", targetNamespace = "") HentBehandlingRequest hentBehandlingRequest) {
        return mockData.hentBehandlingResponse();
    }

    @Override
    @RequestWrapper(localName = "ping", targetNamespace = "http://nav.no/tjeneste/domene/brukerdialog/henvendelsesbehandling/v1/", className = "no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.Ping")
    @WebMethod
    @ResponseWrapper(localName = "pingResponse", targetNamespace = "http://nav.no/tjeneste/domene/brukerdialog/henvendelsesbehandling/v1/", className = "no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.PingResponse")
    public void ping() {
    }
}
