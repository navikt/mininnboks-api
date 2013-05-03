package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.sbl.dialogarena.common.jetty.Jetty;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;

public final class StartJetty {

    public static final int PORT = 8080;

    private StartJetty() {
    }

    public static void main(String[] args) throws Exception {

        System.setProperty("spring.profiles.active", "test");
        System.setProperty("wicket.configuration", "development");
        SystemProperties.load("/environment-test.properties");

        Jetty jetty = usingWar(WEBAPP_SOURCE).at("minehenvendelser").port(PORT).buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

}
