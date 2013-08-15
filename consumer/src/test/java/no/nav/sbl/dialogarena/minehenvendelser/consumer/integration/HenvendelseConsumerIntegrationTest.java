package no.nav.sbl.dialogarena.minehenvendelser.consumer.integration;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.HenvendelseConsumerTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.meldinger.HentBrukerBehandlingListeResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createUnderArbeidBehandling;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HenvendelseConsumerTestContext.class})
public class HenvendelseConsumerIntegrationTest {

    @Inject
    private BehandlingService service;

    @Inject
    private MockData mockdata;

    @Test
    public void shouldIntegrateWithHenvendelserViaWebService() {
        final String foedselsnummer = "***REMOVED***";
        mockdata.getHentData().addResponse(foedselsnummer, new HentBrukerBehandlingListeResponse().withBrukerBehandlinger(createUnderArbeidBehandling()));
        List<Henvendelsesbehandling> henvendelsesbehandlingList = service.hentPabegynteBehandlinger(foedselsnummer);
        assertNotNull(henvendelsesbehandlingList);
        assertThat(henvendelsesbehandlingList.size(), equalTo(1));
    }

    @Test
    public void ukjentBrukerSkalGiTomListe() {
        mockdata.getHentData().addResponse("tester", new HentBrukerBehandlingListeResponse().withBrukerBehandlinger(createUnderArbeidBehandling()));
        List<Henvendelsesbehandling> henvendelsesbehandlingList = service.hentPabegynteBehandlinger("test1");
        assertNotNull(henvendelsesbehandlingList);
        assertThat(henvendelsesbehandlingList.size(), equalTo(0));
    }

}
