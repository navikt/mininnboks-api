package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.sbl.dialogarena.common.jetty.Jetty;

import java.io.File;

import static java.lang.System.setProperty;
import static no.nav.modig.core.context.SubjectHandler.SUBJECTHANDLER_KEY;
import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.minehenvendelser.SystemProperties.load;

public final class StartJetty {

    public static final int PORT = 8082;

    public static final String AKTOR_ID = "***REMOVED***";

    @SuppressWarnings({ "PMD.SystemPrintln" })
    public static void main(String[] args) {
        setProperty("java.security.auth.login.config", "src/test/resources/login.conf");
        setProperty("wicket.configuration", "development");
        setProperty(SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());

        load("/environment-test.properties");
        setupKeyAndTrustStore();

        Jetty jetty = usingWar(WEBAPP_SOURCE).at("minehenvendelser").port(PORT)
                .overrideWebXml(new File(TEST_RESOURCES, "override-web.xml"))
                .buildJetty();
        System.out.println("ADDRESS: " + jetty.getBaseUrl());
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

}
