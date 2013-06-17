package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.sbl.dialogarena.common.jetty.Jetty;

import java.io.File;

import static java.lang.System.setProperty;
import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.modig.testcertificates.TestCertificates.setupKeyAndTrustStore;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;
import static no.nav.sbl.dialogarena.minehenvendelser.SystemProperties.load;

public final class StartJetty {

    public static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());
        setProperty("java.security.auth.login.config", "src/test/resources/login.conf");
        setProperty("spring.profiles.active", "test");
        setProperty("wicket.configuration", "development");
        load("/environment-test.properties");

        Jetty jetty = usingWar(WEBAPP_SOURCE).at("minehenvendelser").port(PORT).overrideWebXml(new File(TEST_RESOURCES, "override-web.xml")).buildJetty();
        setupKeyAndTrustStore();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

}
