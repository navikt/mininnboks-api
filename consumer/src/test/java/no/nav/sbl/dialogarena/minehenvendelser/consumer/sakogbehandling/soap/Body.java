package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.soap;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.FinnSakOgBehandlingskjedeListeResponse;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Body", namespace = Envelope.HTTP_SCHEMAS_XMLSOAP_ORG_SOAP_ENVELOPE)
public class Body {

    @XmlElement(namespace = "http://nav.no/tjeneste/virksomhet/sakOgBehandling/v1/")
    public FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListeResponse;

    public Body(FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListeResponse) {
        this.finnSakOgBehandlingskjedeListeResponse = finnSakOgBehandlingskjedeListeResponse;
    }

    public Body() { }
}
