package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.sbl.dialogarena.common.jetty.Jetty;

import static java.lang.System.setProperty;
import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.minehenvendelser.SystemProperties.load;

public final class StartJetty {

    public static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        setProperty("spring.profiles.active", "test");
        setProperty("wicket.configuration", "development");
        load("/environment-test.properties");

        Jetty jetty = usingWar(WEBAPP_SOURCE).at("minehenvendelser").port(PORT).buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

}
