package no.nav.sbl.dialogarena.mininnboks.servlet;

import no.nav.modig.core.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ApplicationServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServlet.class);

    @Override
    protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        response.setContentType("text/html");
        try {
            try (InputStream input = getServletContext().getResourceAsStream("/index.html");
                 OutputStream output = response.getOutputStream()) {

                int bufferLength = 4096;
                byte[] bytes = new byte[bufferLength];
                int read = input.read(bytes, 0, bufferLength);
                while (read != -1) {
                    output.write(bytes, 0, read);
                    output.flush();
                    read = input.read(bytes, 0, bufferLength);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Servlet error", e);
            throw new ApplicationException("Servlet error");
        }
    }
}
