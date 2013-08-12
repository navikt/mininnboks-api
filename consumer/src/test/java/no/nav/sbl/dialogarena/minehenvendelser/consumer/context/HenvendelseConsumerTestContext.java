package no.nav.sbl.dialogarena.minehenvendelser.consumer.context;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingsServicePort;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.integration.HentBehandlingWebServiceMock;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBehandlingsstatus;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.meldinger.HentBrukerBehandlingListeResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.webbitserver.WebServer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_1;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_2;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_7;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_8;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.KODEVERK_ID_9;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createWSDokumentForventningMock;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createWsBehandlingMock;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBehandlingsstatus.UNDER_ARBEID;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.IKKE_VALGT;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.LASTET_OPP;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSInnsendingsValg.SENDES_IKKE;
import static org.webbitserver.WebServers.createWebServer;

@Configuration
public class HenvendelseConsumerTestContext {

    @Value("${test.henvendelser.ws.url}")
    private URL endpoint;

    @Bean
    public static PropertyPlaceholderConfigurer placeholderConfigurer() {
        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource("environment-test.properties"));
        return placeholderConfigurer;
    }

    @Bean
    public BehandlingService behandlingService() {
        return new BehandlingsServicePort();
    }

    @Bean
    public MockData mockData() {
        MockData mockData = new MockData();
        mockData.getHentData().addResponse("***REMOVED***", new HentBrukerBehandlingListeResponse().withBrukerBehandlinger(createUnderArbeidBehandling(), createUnderArbeidEttersendingBehandling()));
        mockData.getHentData().addResponse("test", new HentBrukerBehandlingListeResponse().withBrukerBehandlinger(createFitnesseTestData()));
        return mockData;
    }

    @Bean
    public WebServer webbitWebserver() throws InterruptedException {
        try {
            return createWebServer(endpoint.getPort()).add(endpoint.getPath(), new HentBehandlingWebServiceMock(mockData())).start().get();
        } catch (ExecutionException e) {
            throw new ApplicationException("Stopp Jetty!!!", e);
        }
    }

    @Bean
    public HenvendelsesBehandlingPortType getHenvendelsesBehandlingPortType() {
        JaxWsProxyFactoryBean proxyFactoryBean = new JaxWsProxyFactoryBean();
        proxyFactoryBean.setServiceClass(HenvendelsesBehandlingPortType.class);
        proxyFactoryBean.setAddress(endpoint.toString());
        return proxyFactoryBean.create(HenvendelsesBehandlingPortType.class);
    }

    private static WSBrukerBehandlingOppsummering createUnderArbeidBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 4, 1, 1), new DateTime(2013, 1, 4, 1, 1), UNDER_ARBEID, false);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createWSDokumentForventningMock(true, KODEVERK_ID_7, IKKE_VALGT),
                createWSDokumentForventningMock(false, KODEVERK_ID_8, IKKE_VALGT));
        return wsBehandlingMock;
    }

    private static List<WSBrukerBehandlingOppsummering> createFitnesseTestData() {
        List<WSBrukerBehandlingOppsummering> behandlinger = new ArrayList<>();
        WSBrukerBehandlingOppsummering behandling1 = createWsBehandlingMock(null, new DateTime(2012, 9, 19, 1, 18), WSBehandlingsstatus.UNDER_ARBEID);
        WSBrukerBehandlingOppsummering behandling2 = createWsBehandlingMock(null, new DateTime(2012, 9, 19, 1, 18), WSBehandlingsstatus.UNDER_ARBEID);

        behandling1.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createWSDokumentForventningMock(true, KODEVERK_ID_1, LASTET_OPP),
                createWSDokumentForventningMock(false, KODEVERK_ID_2, SENDES_IKKE)
        );

        behandling2.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createWSDokumentForventningMock(true, KODEVERK_ID_1, LASTET_OPP),
                createWSDokumentForventningMock(false, KODEVERK_ID_2, LASTET_OPP)
        );

        behandlinger.add(behandling1);
        behandlinger.add(behandling2);
        return behandlinger;
    }

    private static WSBrukerBehandlingOppsummering createUnderArbeidEttersendingBehandling() {
        WSBrukerBehandlingOppsummering wsBehandlingMock = createWsBehandlingMock(new DateTime(2013, 1, 5, 1, 1), new DateTime(2013, 1, 5, 1, 1), WSBehandlingsstatus.UNDER_ARBEID, true);
        wsBehandlingMock.getDokumentForventningOppsummeringer().withDokumentForventningOppsummering(
                createWSDokumentForventningMock(true, KODEVERK_ID_1, WSInnsendingsValg.IKKE_VALGT),
                createWSDokumentForventningMock(false, KODEVERK_ID_9, WSInnsendingsValg.IKKE_VALGT));
        return wsBehandlingMock;
    }
}
