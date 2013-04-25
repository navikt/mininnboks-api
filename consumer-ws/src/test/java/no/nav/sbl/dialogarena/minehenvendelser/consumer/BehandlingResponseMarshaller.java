package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.soap.Body;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.soap.Envelope;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.soap.Header;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HentBrukerBehandlingerResponse;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.io.IOUtils;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;

public class BehandlingResponseMarshaller implements Transformer<HentBrukerBehandlingerResponse, String> {
    private final Jaxb2Marshaller jaxb2Marshaller;

    public BehandlingResponseMarshaller(Jaxb2Marshaller jaxb2Marshaller) {
        this.jaxb2Marshaller = jaxb2Marshaller;
    }


    @Override
    public String transform(HentBrukerBehandlingerResponse hentBrukerBehandlingerResponse) {
        Writer writer = new StringWriter();
        jaxb2Marshaller.marshal(inEnvelope(hentBrukerBehandlingerResponse), new StreamResult(writer));
        IOUtils.closeQuietly(writer);
        return writer.toString();
    }


    private Envelope inEnvelope(HentBrukerBehandlingerResponse hentBrukerBehandlingerResponse)  {
        return new Envelope(new Header(), new Body(hentBrukerBehandlingerResponse));
    }


}
