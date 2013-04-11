package no.nav.sbl.dialogarena.minehenvendelser;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;

import java.io.IOException;

import no.nav.sbl.dialogarena.common.jetty.Jetty;


public final class StartJetty {

    public static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        Jetty jetty = usingWar(WEBAPP_SOURCE).at("minehenvendelser").port(PORT).buildJetty();

        System.setProperty("spring.profiles.active", "test");
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

    private StartJetty() { }

}
