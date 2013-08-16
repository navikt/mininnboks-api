package no.nav.sbl.dialogarena.minehenvendelser;

import static no.nav.modig.core.context.SubjectHandler.SUBJECTHANDLER_KEY;
import static no.nav.modig.lang.collections.FactoryUtils.gotKeypress;
import static no.nav.modig.lang.collections.RunnableUtils.first;
import static no.nav.modig.lang.collections.RunnableUtils.waitFor;
import static no.nav.modig.test.util.FilesAndDirs.TEST_RESOURCES;
import static no.nav.modig.test.util.FilesAndDirs.WEBAPP_SOURCE;
import static no.nav.sbl.dialogarena.common.jetty.Jetty.usingWar;

import java.io.File;
import java.lang.reflect.Proxy;

import no.nav.modig.core.context.StaticSubjectHandler;
import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.modig.testcertificates.TestCertificates;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

public final class StartJetty {

	public static final int PORT = 8082;
//
//	public static void main(String[] args) {
//
//		System.setProperty("no.nav.modig.security.sts.url", "http://localhost:9080/SecurityTokenServiceProvider/");
//		System.setProperty("no.nav.modig.security.systemuser.username", "BD03");
//		System.setProperty("no.nav.modig.security.systemuser.password", "CHANGEME");
//		System.setProperty(SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());
//
//		JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
//		proxyFactoryBean.setWsdlLocation("sakOgBehandling/no/nav/tjeneste/virksomhet/sakOgBehandling/v1/SakOgBehandling.wsdl");
//		proxyFactoryBean.setAddress("http://localhost:55669/path/to/sakogbehandling/service");
//		proxyFactoryBean.setServiceClass(SakOgBehandlingPortType.class);
//		proxyFactoryBean.getFeatures().add(new WSAddressingFeature());
//		proxyFactoryBean.getFeatures().add(new LoggingFeature());
//		SakOgBehandlingPortType sakOgBehandlingPortType = proxyFactoryBean.create(SakOgBehandlingPortType.class);
//		STSConfigurationUtility.configureStsForExternalSSO(((ClientProxy) Proxy.getInvocationHandler(sakOgBehandlingPortType)).getClient());
//		sakOgBehandlingPortType.ping();
//	}

	@SuppressWarnings({ "PMD.SystemPrintln" })
	public static void main(String[] args) {
		System.setProperty("java.security.auth.login.config", "src/test/resources/login.conf");
		System.setProperty("wicket.configuration", "development");
		System.setProperty("disable.ssl.cn.check", "true");
		System.setProperty(SUBJECTHANDLER_KEY, StaticSubjectHandler.class.getName());

		SystemProperties.load("/environment-test.properties");
		TestCertificates.setupKeyAndTrustStore();

		Jetty jetty = usingWar(WEBAPP_SOURCE).at("minehenvendelser").port(PORT)
				.overrideWebXml(new File(TEST_RESOURCES, "override-web.xml")).buildJetty();
		System.out.println("ADDRESS: " + jetty.getBaseUrl());
		jetty.startAnd(first(waitFor(gotKeypress())).then(jetty.stop));
	}

}
