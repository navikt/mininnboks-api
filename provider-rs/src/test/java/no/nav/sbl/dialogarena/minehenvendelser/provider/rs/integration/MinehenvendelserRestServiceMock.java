package no.nav.sbl.dialogarena.minehenvendelser.provider.rs.integration;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

public class MinehenvendelserRestServiceMock implements HttpHandler {

    @Override
    public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
        String body = request.body();

        String message;
        if(body.contains("ping")) {

        } else {

        }
    }
}
