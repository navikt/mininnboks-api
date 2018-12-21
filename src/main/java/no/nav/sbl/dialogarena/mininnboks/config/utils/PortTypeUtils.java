package no.nav.sbl.dialogarena.mininnboks.config.utils;

import lombok.Builder;
import no.nav.apiapp.selftest.Helsesjekk;
import no.nav.apiapp.selftest.HelsesjekkMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.util.fn.UnsafeConsumer;

public class PortTypeUtils {

    public static <T> PortType<T> createPortType(String address, String wsdlUrl, Class<T> serviceClass, boolean externalService, UnsafeConsumer<T> ping) {
//        JaxWsProxyFactoryBean proxy = new JaxWsProxyFactoryBean();
//        if (isNotBlank(wsdlUrl)) {
//            proxy.setWsdlURL(wsdlUrl);
//        }
//        proxy.setAddress(address);
//        proxy.setServiceClass(serviceClass);
//        proxy.getFeatures().addAll(Arrays.asList(new WSAddressingFeature(), new LoggingFeature()));
//        proxy.setProperties(new HashMap<String, Object>());
//        proxy.getProperties().put("jaxb.additionalContextClasses", new Class[]{
//                XMLHenvendelse.class,
//                XMLMetadataListe.class,
//                XMLMeldingFraBruker.class,
//                XMLMeldingTilBruker.class});

//        T portType = proxy.create(serviceClass);
//        Client client = ClientProxy.getClient(portType);
//        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
//        httpConduit.setTlsClientParameters(new TLSClientParameters());
//        if (Boolean.valueOf(System.getProperty("disable.ssl.cn.check", "false"))) {
//            httpConduit.getTlsClientParameters().setDisableCNCheck(true);
//        }
//        if (externalService) {
//            STSConfigurationUtil.configureStsForExternalSSO(client);
//        } else {
//            STSConfigurationUtil.configureStsForSystemUserInFSS(client);
//        }

        CXFClient<T> tcxfClient = clientBuilder(address, serviceClass, wsdlUrl);
        if (externalService) {
            tcxfClient.configureStsForOnBehalfOfWithJWT();
        } else {
            tcxfClient.configureStsForSystemUser();
//            STSConfigurationUtil.configureStsForSystemUserInFSS(client);
        }

        T portType = tcxfClient.build();
        T pingPort = clientBuilder(address, serviceClass, wsdlUrl).configureStsForSystemUser().build();

        HelsesjekkMetadata helsesjekkMetadata = new HelsesjekkMetadata(serviceClass.getName(), address, serviceClass.getName(), false);
        Helsesjekk helsesjekk = new Helsesjekk() {
            @Override
            public void helsesjekk() {
                ping.accept(pingPort);
            }

            @Override
            public HelsesjekkMetadata getMetadata() {
                return helsesjekkMetadata;
            }
        };

        return PortType.<T>builder()
                .portType(portType)
                .helsesjekk(helsesjekk)
                .build();
    }

    private static <T> CXFClient<T> clientBuilder(String address, Class<T> serviceClass, String wsdlUrl) {
        return new CXFClient<>(serviceClass)
                .address(address)
                .wsdl(wsdlUrl)
                .withProperty("jaxb.additionalContextClasses", new Class[]{
                        XMLHenvendelse.class,
                        XMLMetadataListe.class,
                        XMLMeldingFraBruker.class,
                        XMLMeldingTilBruker.class
                });
    }

    @Builder
    public static class PortType<T> {
        public T portType;
        public Helsesjekk helsesjekk;
    }
}
