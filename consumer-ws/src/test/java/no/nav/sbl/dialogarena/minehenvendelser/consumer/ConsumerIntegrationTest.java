package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConsumerTestContext.class})
public class ConsumerIntegrationTest {

    @Inject
    private BehandlingService service;

    @Test
    @Ignore
    public void shouldIntegrateWithHenvendelserViaWebService() {
        List<Behandling> behandlingList = service.hentBehandlinger("test");
        assertNotNull(behandlingList);
        assertThat(behandlingList.size(), equalTo(1));
    }

    @Test
    public void ukjentBrukerSkalGiTomListe() {
        List<Behandling> behandlingList = service.hentBehandlinger("test1");
        assertNotNull(behandlingList);
        assertThat(behandlingList.size(), equalTo(0));
    }
}
