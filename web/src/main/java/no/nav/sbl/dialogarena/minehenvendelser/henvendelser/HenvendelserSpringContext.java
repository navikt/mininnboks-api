package no.nav.sbl.dialogarena.minehenvendelser.henvendelser;

import java.util.HashMap;

import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.security.SecurityConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HenvendelserSpringContext {

	@Value("${henvendelser.webservice.sporsmal.url}")
	protected String spmSvarEndpoint;

	@Value("${henvendelser.webservice.felles.url}")
	protected String henvendelseEndpoint;

	@Bean
	public SporsmalOgSvarPortType sporsmalOgSvarPortType() {
		return createPortType(spmSvarEndpoint, "classpath:SporsmalOgSvar.wsdl", SporsmalOgSvarPortType.class);
	}

	@Bean
	public HenvendelsePortType henvendelsePortType() {
		return createPortType(henvendelseEndpoint, "classpath:Henvendelse.wsdl", HenvendelsePortType.class);
	}
	
	@Bean
	public MeldingService meldingService() {
		return new MeldingService();
	}

	private static <T> T createPortType(String address, String wsdlUrl, Class<T> serviceClass) {
		JaxWsProxyFactoryBean proxy = new JaxWsProxyFactoryBean();
		proxy.getFeatures().add(new WSAddressingFeature());
		proxy.getFeatures().add(new LoggingFeature());
		proxy.setServiceClass(serviceClass);
		proxy.setAddress(address);
		proxy.setWsdlURL(wsdlUrl);
		proxy.setProperties(new HashMap<String, Object>());
		proxy.getProperties().put(SecurityConstants.MUSTUNDERSTAND, false);

		T portType = proxy.create(serviceClass);
		Client client = ClientProxy.getClient(portType);
		HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
		httpConduit.setTlsClientParameters(new TLSClientParameters());
		if (Boolean.valueOf(System.getProperty("disable.ssl.cn.check", "false"))) {
			httpConduit.getTlsClientParameters().setDisableCNCheck(true);
		}
		STSConfigurationUtility.configureStsForExternalSSO(client);
		return portType;
	}

}
