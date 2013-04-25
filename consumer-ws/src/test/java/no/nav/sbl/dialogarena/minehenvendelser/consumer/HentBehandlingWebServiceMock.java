package no.nav.sbl.dialogarena.minehenvendelser.consumer;


import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HentBrukerBehandlingerResponse;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import java.nio.charset.Charset;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createWsBehandlingMock;

public class HentBehandlingWebServiceMock implements HttpHandler {

    private final BehandlingResponseMarshaller marshaller;
    private final MockData mockData;

    public HentBehandlingWebServiceMock(BehandlingResponseMarshaller marshaller, MockData mockData) {
        this.mockData = mockData;
        this.marshaller = marshaller;

    }

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse response, HttpControl control) throws Exception {
        String request = httpRequest.body();
        String aktorId = Jsoup.parse(request, "", Parser.xmlParser()).select("aktorId").get(0).text();

        String message = marshaller.transform(mockData.getData(aktorId));

        response.charset(Charset.forName("UTF-8"))
                .header("Content-Type", "text/xml; charset=UTF-8")
                .content(message)
                .end();
    }

}
