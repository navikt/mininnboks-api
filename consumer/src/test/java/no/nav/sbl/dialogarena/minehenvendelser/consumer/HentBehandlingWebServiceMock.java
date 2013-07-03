package no.nav.sbl.dialogarena.minehenvendelser.consumer;


import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import java.nio.charset.Charset;

public class HentBehandlingWebServiceMock implements HttpHandler {

    private final BehandlingResponseMarshaller marshaller;
    private final MockData mockData;

    public HentBehandlingWebServiceMock(MockData mockData) {
        this.mockData = mockData;
        this.marshaller = new BehandlingResponseMarshaller(createMarshaller());
    }

    private Jaxb2Marshaller createMarshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(
                no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.ObjectFactory.class,
                no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.ObjectFactory.class,
                no.nav.sbl.dialogarena.minehenvendelser.consumer.soap.ObjectFactory.class);
        return jaxb2Marshaller;
    }

    @Override
    public void handleHttpRequest(HttpRequest httpRequest, HttpResponse response, HttpControl control) throws Exception {
        String body = httpRequest.body();

        String message;
        if (body.contains("ping")) {
            message = getPingResponse();
        } else {
            String aktorId = Jsoup.parse(body, "", Parser.xmlParser()).select("aktorId").get(0).text();
            message = marshaller.transform(mockData.getData(aktorId));
        }

        response.charset(Charset.forName("UTF-8"))
                .header("Content-Type", "text/xml; charset=UTF-8")
                .content(message)
                .end();
    }

    private String getPingResponse() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "   <soap:Body>\n" +
                "      <ns3:pingResponse xmlns:ns3=\"http://nav.no/tjeneste/domene/brukerdialog/henvendelsesbehandling/v1\" xmlns:ns2=\"http://nav.no/tjeneste/domene/brukerdialog/henvendelse/v1/informasjon\">\n" +
                "         <return>true</return>\n" +
                "      </ns3:pingResponse>\n" +
                "   </soap:Body>\n" +
                "</soap:Envelope>\n";
    }

}
