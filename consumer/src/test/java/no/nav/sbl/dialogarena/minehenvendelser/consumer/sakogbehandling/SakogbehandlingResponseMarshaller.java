package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.soap.Body;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.soap.Envelope;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.soap.Header;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingskjedensBehandlingerResponse;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class SakogbehandlingResponseMarshaller {

    private final Jaxb2Marshaller jaxb2Marshaller;

    public SakogbehandlingResponseMarshaller(Jaxb2Marshaller jaxb2Marshaller) {
        this.jaxb2Marshaller = jaxb2Marshaller;
    }

    public String transformFinnSakOgBehandlingskjedeListeResponse(FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListeResponse) {
        Writer writer = new StringWriter();
        jaxb2Marshaller.marshal(inEnvelope(finnSakOgBehandlingskjedeListeResponse), new StreamResult(writer));
        closeQuietly(writer);
        return writer.toString();
    }

    public String transformHentBehandlingskjedensBehandlingerResponse(HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlingerResponse) {
        Writer writer = new StringWriter();
        jaxb2Marshaller.marshal(inEnvelope(hentBehandlingskjedensBehandlingerResponse), new StreamResult(writer));
        closeQuietly(writer);
        return writer.toString();
    }

    private Envelope inEnvelope(FinnSakOgBehandlingskjedeListeResponse finnSakOgBehandlingskjedeListeResponse) {
        return new Envelope(new Header(), new Body(finnSakOgBehandlingskjedeListeResponse));
    }

    private Envelope inEnvelope(HentBehandlingskjedensBehandlingerResponse hentBehandlingskjedensBehandlingerResponse) {
        return new Envelope(new Header(), new Body(hentBehandlingskjedensBehandlingerResponse));
    }
}
