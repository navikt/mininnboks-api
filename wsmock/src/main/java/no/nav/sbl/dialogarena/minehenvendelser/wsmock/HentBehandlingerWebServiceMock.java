package no.nav.sbl.dialogarena.minehenvendelser.wsmock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import javax.inject.Inject;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.nio.charset.Charset;

public class HentBehandlingerWebServiceMock implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(HentBehandlingerWebServiceMock.class);

    @Inject
    private Jaxb2Marshaller jaxb2Marshaller;

    private final MockData mockData;

    public HentBehandlingerWebServiceMock(MockData mockData) {
        this.mockData = mockData;
    }

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        logger.info("entered handleHttpRequest");
        StringWriter writer = new StringWriter();
        jaxb2Marshaller.marshal(mockData.getBehandlingerResponse(), new StreamResult(writer));
        String message = writer.toString();
        logger.info("marshalled the response object");
        response.charset(Charset.forName("UTF-8"))
                .header("Content-Type", "text/xml; charset=UTF-8")
                .content(message)
                .end();
        logger.info("handleHttpRequest done");
    }
}
