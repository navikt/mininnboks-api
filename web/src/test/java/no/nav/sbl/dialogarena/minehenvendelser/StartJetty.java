package no.nav.sbl.dialogarena.minehenvendelser;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;

import java.net.URL;
import java.util.Properties;

import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.HentBehandlingWebServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;

import org.webbitserver.WebServer;
import org.webbitserver.WebServers;

public final class StartJetty {

    public static final int PORT = 8080;

    public static void main(String[] args) throws Exception {

        Properties properties = SystemProperties.load("/environment-test.properties");
        URL url = new URL((String) properties.get("henvendelser.ws.url"));

        WebServer server = WebServers.createWebServer(url.getPort()).add(url.getPath(), new HentBehandlingWebServiceMock(new MockData().createDummyData()));
        server.start().get();

        Jetty jetty = usingWar(WEBAPP_SOURCE).at("minehenvendelser").port(PORT).buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

    private StartJetty() {
    }

}
