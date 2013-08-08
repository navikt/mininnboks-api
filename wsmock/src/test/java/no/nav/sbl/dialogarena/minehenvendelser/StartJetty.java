package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.sbl.dialogarena.common.jetty.Jetty;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;

public final class StartJetty {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        Jetty jetty = usingWar(WEBAPP_SOURCE).at("wsmock").port(PORT).buildJetty();
//        jetty.start();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }


    private StartJetty() { }
}
