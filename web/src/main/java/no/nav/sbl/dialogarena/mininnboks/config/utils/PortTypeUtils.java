package no.nav.sbl.dialogarena.mininnboks.config.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.security.sts.utility.STSConfigurationUtility;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import java.util.Arrays;
import java.util.HashMap;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class PortTypeUtils {
    public static <T> T createPortType(String address, String wsdlUrl, Class<T> serviceClass, boolean externalService) {
        JaxWsProxyFactoryBean proxy = new JaxWsProxyFactoryBean();
        if (isNotBlank(wsdlUrl)) {
            proxy.setWsdlURL(wsdlUrl);
        }
        proxy.setAddress(address);
        proxy.setServiceClass(serviceClass);
        proxy.getFeatures().addAll(Arrays.asList(new WSAddressingFeature(), new LoggingFeature()));
        proxy.setProperties(new HashMap<String, Object>());
        proxy.getProperties().put("jaxb.additionalContextClasses", new Class[]{
                XMLHenvendelse.class,
                XMLMetadataListe.class,
                XMLMeldingFraBruker.class,
                XMLMeldingTilBruker.class});

        T portType = proxy.create(serviceClass);
        Client client = ClientProxy.getClient(portType);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        httpConduit.setTlsClientParameters(new TLSClientParameters());
        if (Boolean.valueOf(System.getProperty("disable.ssl.cn.check", "false"))) {
            httpConduit.getTlsClientParameters().setDisableCNCheck(true);
        }
        if (externalService) {
            STSConfigurationUtility.configureStsForExternalSSO(client);
        } else {
            STSConfigurationUtility.configureStsForSystemUser(client);
        }
        return portType;
    }
}
