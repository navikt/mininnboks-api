package no.nav.sbl.dialogarena.minehenvendelser.consumer.integration;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.meldinger.HentBrukerBehandlingListeResponse;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFerdigBehandling;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConsumerTestContext.class})
public class ConsumerIntegrationTest {

    @Inject
    private BehandlingService service;

    @Inject
    private MockData mockdata;

    @Test
    public void shouldIntegrateWithHenvendelserViaWebService() {
        mockdata.getHentData().addResponse("***REMOVED***", new HentBrukerBehandlingListeResponse().withBrukerBehandlinger(createFerdigBehandling()));
        List<Behandling> behandlingList = service.hentBehandlinger("***REMOVED***");
        assertNotNull(behandlingList);
        assertThat(behandlingList.size(), equalTo(1));
    }

    @Test
    public void ukjentBrukerSkalGiTomListe() {
        mockdata.getHentData().addResponse("tester", new HentBrukerBehandlingListeResponse().withBrukerBehandlinger(createFerdigBehandling()));
        List<Behandling> behandlingList = service.hentBehandlinger("test1");
        assertNotNull(behandlingList);
        assertThat(behandlingList.size(), equalTo(0));
    }

    @After
    public void clearData() {
        mockdata.getHentData().clear();
    }
}
