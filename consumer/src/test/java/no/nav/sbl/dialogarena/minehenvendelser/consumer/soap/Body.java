package no.nav.sbl.dialogarena.minehenvendelser.consumer.soap;

import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HentBrukerBehandlingerResponse;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Body", namespace = Envelope.HTTP_SCHEMAS_XMLSOAP_ORG_SOAP_ENVELOPE)
public class Body {

    @XmlElement(namespace = "http://nav.no/tjeneste/virksomhet/henvendelsesbehandling/v1")
    public HentBrukerBehandlingerResponse hentBrukerBehandlingerResponse;

    public Body(HentBrukerBehandlingerResponse hentBrukerBehandlingerResponse) {
        this.hentBrukerBehandlingerResponse = hentBrukerBehandlingerResponse;
    }

    public Body() { }

}
