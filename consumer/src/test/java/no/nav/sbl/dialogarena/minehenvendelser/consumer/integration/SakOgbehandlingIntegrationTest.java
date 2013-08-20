package no.nav.sbl.dialogarena.minehenvendelser.consumer.integration;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.SakogbehandlingIntegrationTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingOppsummering;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSDokumentForventningOppsummeringer;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesbehandling.v1.HenvendelsesBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.HentBehandlingskjedensBehandlingerResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Applikasjoner;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Avslutningsstatuser;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandling;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.BehandlingVS;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingsstatuser;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingsstegtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Behandlingstyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.hentbehandlingskjedensbehandlinger.Behandlingskjede;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.AKTOR_ID;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFinnSakOgBehandlingskjedeListeResponse;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createXmlGregorianDate;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.populateFinnbehandlingKjedeListWithOneWithNeitherUnderArbeidNorFerdig;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.populateFinnbehandlingKjedeListWithThreeFerdige;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.populateFinnbehandlingKjedeListWithTwoUnderArbeid;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBehandlingsstatus.FERDIG;
import static no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.informasjon.WSBrukerBehandlingType.DOKUMENT_BEHANDLING;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SakogbehandlingIntegrationTestContext.class})
public class SakOgbehandlingIntegrationTest {

    @Inject
    private SakogbehandlingService service;

    @Inject
    private MockData mockData;

    @Inject
    private HenvendelsesBehandlingPortType henvendelsesBehandlingPortType;

    @After
    public void after() {
        mockData.getFinnData().clear();
    }

    @Test
    public void verifyNumberOfFerdigeSoeknader() {
        mockData.getFinnData().addResponse(AKTOR_ID, createFinnSakOgBehandlingskjedeListeResponse(populateFinnbehandlingKjedeListWithThreeFerdige()));
        List<Soeknad> soeknadList = service.finnFerdigeSoeknader(AKTOR_ID);
        assertNotNull(soeknadList);
        assertThat(soeknadList.size(), equalTo(3));
    }

    @Test
    public void verifyNumberOfSoeknaderUnderArbeid() {
        mockData.getFinnData().addResponse(AKTOR_ID, createFinnSakOgBehandlingskjedeListeResponse(populateFinnbehandlingKjedeListWithTwoUnderArbeid()));
        List<Soeknad> soeknadList = service.finnSoeknaderUnderArbeid(AKTOR_ID);
        assertNotNull(soeknadList);
        assertThat(soeknadList.size(), equalTo(2));
    }

    @Test
    public void verifyNumberOfMottatteSoeknader() {
        setupOneMottattSoeknad(AKTOR_ID);
        List<Soeknad> soeknadList = service.finnMottatteSoeknader(AKTOR_ID);
        assertNotNull(soeknadList);
        assertThat(soeknadList.size(), equalTo(1));
    }

    private void setupOneMottattSoeknad(String aktorId) {
        String behandlingsId = setupHenvendelseForMottattSoeknad(aktorId);
        setupSakogbehandlingForMottattSoeknad(behandlingsId, aktorId);
    }

    private void setupSakogbehandlingForMottattSoeknad(String behandlingsId, String aktorId) {
        String behandlingsKjedeId = "behandlingsKjedeId";
        mockData.getFinnData().addResponse(aktorId, createFinnSakOgBehandlingskjedeListeResponse(populateFinnbehandlingKjedeListWithOneWithNeitherUnderArbeidNorFerdig(behandlingsKjedeId)));
        mockData.getMockHentBehandlingskjedensBehandlingerData().addResponse(behandlingsKjedeId, new HentBehandlingskjedensBehandlingerResponse().withResponse(new no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.HentBehandlingskjedensBehandlingerResponse().withBehandlingskjede(new Behandlingskjede().withBehandlingskjedeId(behandlingsKjedeId).withBehandling(createBehandlingForSakogbehandling(behandlingsId)))));
    }

    private Behandling[] createBehandlingForSakogbehandling(String behandlingsId) {
        BehandlingVS behandling = new BehandlingVS()
                .withBehandlingsId(behandlingsId)
                .withBehandlingstype(new Behandlingstyper().withValue("type"))
                .withApplikasjon(new Applikasjoner().withValue("applikasjon"))
                .withBehandlingsstatus(new Behandlingsstatuser().withValue("behandlingsstatus"))
                .withSisteBehandlingssteg(new Behandlingsstegtyper().withValue("stegstype"))
                .withStart(createXmlGregorianDate(1, 2, 2013))
                .withAvslutningsstatus(new Avslutningsstatuser().withValue("avslutningsstatus"))
                .withNormertBehandlingstid(new Behandlingstid())
                .withFrist(createXmlGregorianDate(1, 3, 2013));

        return new Behandling[]{behandling};
    }

    private String setupHenvendelseForMottattSoeknad(String aktorId) {
        String behandlingsId = "BEHID1";
        when(henvendelsesBehandlingPortType.hentBrukerBehandlingListe(aktorId)).thenReturn(createBrukerbehandling(behandlingsId));
        return behandlingsId;
    }

    private List<WSBrukerBehandlingOppsummering> createBrukerbehandling(String behandlingsId) {
        List<WSBrukerBehandlingOppsummering> brukerBehandlingOppsummeringer = new ArrayList<>();
        brukerBehandlingOppsummeringer.add(
                new WSBrukerBehandlingOppsummering()
                        .withStatus(FERDIG)
                        .withBehandlingsId(behandlingsId)
                        .withHovedskjemaId("id")
                        .withBrukerBehandlingType(DOKUMENT_BEHANDLING)
                        .withSistEndret(new DateTime())
                        .withDokumentForventningOppsummeringer(new WSDokumentForventningOppsummeringer())
        );
        return brukerBehandlingOppsummeringer;
    }

}
