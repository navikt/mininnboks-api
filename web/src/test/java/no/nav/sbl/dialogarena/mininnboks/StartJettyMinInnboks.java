package no.nav.sbl.dialogarena.mininnboks;

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
public class StartJettyMinInnboks {

	public static void main(String[] args) {
	    SystemProperties.setFrom("jetty-mininnboks.properties");
        System.setProperty("henvendelse.informasjon.ws.url", "https://localhost:8443/henvendelse/services/domene.Brukerdialog/HenvendelseInformasjon_v2");
        System.setProperty("henvendelse.aktivitet.ws.url", "https://localhost:8443/henvendelse/services/domene.Brukerdialog/HenvendelseAktivitet_v2");
		TestCertificates.setupKeyAndTrustStore();

		final Jetty jetty = Jetty.usingWar(FilesAndDirs.WEBAPP_SOURCE).at("mininnboks").port(8585)
				.overrideWebXml(new File(FilesAndDirs.TEST_RESOURCES, "override-web.xml"))
				.buildJetty();
        jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
	}

}
