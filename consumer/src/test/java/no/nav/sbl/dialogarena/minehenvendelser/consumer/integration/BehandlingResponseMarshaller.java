package no.nav.sbl.dialogarena.minehenvendelser.consumer.integration;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.soap.Body;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.soap.Envelope;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.soap.Header;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.meldinger.HentBrukerBehandlingListeResponse;
import org.apache.commons.collections15.Transformer;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class BehandlingResponseMarshaller implements Transformer<HentBrukerBehandlingListeResponse, String> {

    private final Jaxb2Marshaller jaxb2Marshaller;

    public BehandlingResponseMarshaller(Jaxb2Marshaller jaxb2Marshaller) {
        this.jaxb2Marshaller = jaxb2Marshaller;
    }

    @Override
    public String transform(HentBrukerBehandlingListeResponse hentBrukerBehandlingListeResponse) {
        Writer writer = new StringWriter();
        jaxb2Marshaller.marshal(inEnvelope(hentBrukerBehandlingListeResponse), new StreamResult(writer));
        closeQuietly(writer);
        return writer.toString();
    }

    private Envelope inEnvelope(HentBrukerBehandlingListeResponse hentBrukerBehandlingListeResponse)  {
        return new Envelope(new Header(), new Body(hentBrukerBehandlingListeResponse));
    }

}
