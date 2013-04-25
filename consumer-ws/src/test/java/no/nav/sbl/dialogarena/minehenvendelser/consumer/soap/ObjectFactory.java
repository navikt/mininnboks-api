package no.nav.sbl.dialogarena.minehenvendelser.consumer.soap;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public Envelope createEnvelope() {
        return new Envelope();
    }

    public Header createHeader() {
        return new Header();
    }


    public Body createBody() {
        return new Body();
    }

}