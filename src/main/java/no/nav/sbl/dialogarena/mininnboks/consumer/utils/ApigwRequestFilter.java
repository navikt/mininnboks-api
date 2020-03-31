package no.nav.sbl.dialogarena.mininnboks.consumer.utils;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

public class ApigwRequestFilter implements ClientRequestFilter {
    private final String apikey;

    public ApigwRequestFilter(String apikey) {
        this.apikey = apikey;
    }

    @Override
    public void filter(ClientRequestContext request) throws IOException {
        request.getHeaders().putSingle("x-nav-apiKey", apikey);
    }
}
