package no.nav.sbl.dialogarena.minehenvendelser.selftest;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.jar.Manifest;

public class SelfTestPage extends WebPage {

    private static final Logger logger = LoggerFactory.getLogger(SelfTestPage.class);


    public SelfTestPage() {
        String version = getApplicationVersion();
        add(new Label("version", version));
    }

    private String getApplicationVersion() {
        String version = "unknown version";
        try {
            WebRequest req = (WebRequest) RequestCycle.get().getRequest();
            ServletContext servletContext = ((HttpServletRequest) req.getContainerRequest()).getServletContext();
            InputStream inputStream = servletContext.getResourceAsStream(("/META-INF/MANIFEST.MF"));
            Manifest manifest = new Manifest(inputStream);
            version = manifest.getMainAttributes().getValue("Implementation-Version");
        } catch (Exception e) {
            logger.warn("Feil ved henting av applikasjonsversjon: " + e.getMessage());
        }
        return "minehenvendelser - " + version;
    }

}
