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
        CXFClient<T> realClient = clientBuilder(address, serviceClass, wsdlUrl);
        if (externalService) {
            realClient.configureStsForOnBehalfOfWithJWT();
        } else {
            realClient.configureStsForSystemUser();
        }

        T portType = realClient.build();
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
                .port(portType)
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
        public T port;
        public Helsesjekk helsesjekk;
    }
}
