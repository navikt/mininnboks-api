package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import java.nio.charset.Charset;

public class SakogbehandlingWebServiceMock implements HttpHandler {

    private final MockData mockData;
    private final SakogbehandlingResponseMarshaller marshaller;

    public SakogbehandlingWebServiceMock(MockData mockData) {
        this.mockData = mockData;
        this.marshaller = new SakogbehandlingResponseMarshaller(createMarshaller());
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        String body = request.body();

        String message;
        if (body.contains("ping")) {
            message = getPingResponse();
        } else if (body.contains("behandlingskjedeREF")) {
            String behandlingsId = Jsoup.parse(body, "", Parser.xmlParser()).select("behandlingskjedeREF").get(0).text();
            message = marshaller.transformHentBehandlingskjedensBehandlingerResponse(mockData.getMockHentBehandlingskjedensBehandlingerData().getData(behandlingsId));
        } else {
            String aktorId = Jsoup.parse(body, "", Parser.xmlParser()).select("aktoerREF").get(0).text();
            message = marshaller.transformFinnSakOgBehandlingskjedeListeResponse(mockData.getFinnData().getData(aktorId));
        }

        response.charset(Charset.forName("UTF-8"))
                .header("Content-Type", "text/xml; charset=UTF-8")
                .content(message)
                .end();
    }

    private Jaxb2Marshaller createMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(
                no.nav.tjeneste.virksomhet.sakogbehandling.v1.ObjectFactory.class,
                no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.ObjectFactory.class,
                no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.hentbehandlingskjedensbehandlinger.ObjectFactory.class,
                no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.ObjectFactory.class,
                no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.soap.ObjectFactory.class);
        return jaxb2Marshaller;
    }

    private String getPingResponse() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soap:Body>\n" +
                "      <ns3:pingResponse xmlns:ns3=\"http://nav.no/tjeneste/virksomhet/sakOgBehandling/v1/\" xmlns:ns2=\"http://nav.no/tjeneste/virksomhet/sakOgBehandling/v1/informasjon\">\n" +
                "         <return>true</return>\n" +
                "      </ns3:pingResponse>\n" +
                "   </soap:Body>\n" +
                "</soap:Envelope>\n";
    }

}
