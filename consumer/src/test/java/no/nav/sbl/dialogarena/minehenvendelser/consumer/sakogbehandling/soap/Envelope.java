package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.soap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Envelope", namespace = Envelope.HTTP_SCHEMAS_XMLSOAP_ORG_SOAP_ENVELOPE)
public class Envelope {
    static final String HTTP_SCHEMAS_XMLSOAP_ORG_SOAP_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";

    @XmlElement(name = "Header", namespace = Envelope.HTTP_SCHEMAS_XMLSOAP_ORG_SOAP_ENVELOPE)
    public Header header;

    @XmlElement(name = "Body", namespace = Envelope.HTTP_SCHEMAS_XMLSOAP_ORG_SOAP_ENVELOPE)
    public Body body;

    public Envelope(Header header, Body body) {
        this.header = header;
        this.body = body;
    }

    public Envelope() {
    }
}
