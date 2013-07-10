package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;

import java.io.File;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;

public final class StartJetty {

    public static final int PORT = 8082;

    public static void main(String[] args) {
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());
        System.setProperty("java.security.auth.login.config", "src/test/resources/login.conf");
        System.setProperty("wicket.configuration", "development");
        SystemProperties.load("/environment-test.properties");

        TestCertificates.setupKeyAndTrustStore();

        Jetty jetty = usingWar(WEBAPP_SOURCE)
                .at("minehenvendelser")
                .port(PORT)
                .overrideWebXml(new File(TEST_RESOURCES, "override-web.xml"))
                .buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
    }

}
