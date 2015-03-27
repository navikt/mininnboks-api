package no.nav.sbl.dialogarena.mininnboks.servlet;

import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        String path = req.getSession().getServletContext().getRealPath("/");
        try (InputStream htmlFileStream = new FileInputStream(new File(path, "index.html"))) {
            IOUtils.copy(htmlFileStream, resp.getOutputStream());
        }
    }
}
