package no.nav.sbl.dialogarena.minehenvendelser.provider.rs;

import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;

public final class StartJettyRest {
    public static final int PORT = 8083;

    @SuppressWarnings({ "PMD.SystemPrintln" })
    public static void main(String[] args) {
        TestCertificates.setupKeyAndTrustStore();

        Jetty jetty = usingWar(WEBAPP_SOURCE).at("/").port(PORT).buildJetty();
        System.out.println("ADDRESS: " + jetty.getBaseUrl());
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }
}
