package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.core.test.FilesAndDirs;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.test.SystemProperties;

import java.io.File;

import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;

/**
 * Login: Testfamilien Aremark: 10108000398
 */
public class StartJettyMineHenvendelser {

	public static void main(String[] args) {
	    SystemProperties.setFrom("jetty-minehenvendelser.properties");
		TestCertificates.setupKeyAndTrustStore();

		final Jetty jetty = Jetty.usingWar(FilesAndDirs.WEBAPP_SOURCE).at("minehenvendelser").port(8585)
				.overrideWebXml(new File(FilesAndDirs.TEST_RESOURCES, "override-web.xml"))
				.buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
	}

}
