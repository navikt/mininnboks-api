package no.nav.sbl.dialogarena.minehenvendelser.consumer;


import no.nav.tjeneste.virksomhet.henvendelse.v1.informasjon.WSBrukerBehandling;
import no.nav.tjeneste.virksomhet.henvendelsesbehandling.v1.HentBrukerBehandlingerResponse;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import java.nio.charset.Charset;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createWsBehandlingMock;

public class HentBehandlingWebServiceMock implements HttpHandler {

    private final BehandlingResponseMarshaller marshaller;

    public HentBehandlingWebServiceMock(BehandlingResponseMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse response, HttpControl control) throws Exception {
        String request = httpRequest.body();
//        String ident = Jsoup.parse(request, "", Parser.xmlParser()).select("ident").get(0).text();

        String message = marshaller.transform(createMockRespone());

        response.charset(Charset.forName("UTF-8"))
                .header("Content-Type", "text/xml; charset=UTF-8")
                .content(message)
                .end();
    }

    private HentBrukerBehandlingerResponse createMockRespone(){
        HentBrukerBehandlingerResponse response = new HentBrukerBehandlingerResponse();
        WSBrukerBehandling behandling = createWsBehandlingMock( );
        response.withBrukerBehandlinger(behandling);
        return response;
    }


}
