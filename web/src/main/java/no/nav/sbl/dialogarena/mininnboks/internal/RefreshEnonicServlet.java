package no.nav.sbl.dialogarena.mininnboks.internal;

import no.nav.sbl.dialogarena.mininnboks.message.HentNyeTekster;
import org.springframework.web.HttpRequestHandler;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RefreshEnonicServlet implements HttpRequestHandler {

    @Inject
    private HentNyeTekster hentNyeTekster;

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        hentNyeTekster.lastInnNyeInnholdstekster();

        response.setContentType("text/html");
        response.getWriter().write("Nye tekster hentet");
    }
}