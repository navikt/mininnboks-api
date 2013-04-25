package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import javax.inject.Inject;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ConsumerTestContext.class})
public class ConsumerIntegrationTest {

    @Inject
    private BehandlingService service;

    @Test
    @Ignore
    public void shouldIntegrateWithHenvendelserViaWebService() {
        service.hentBehandlinger("test");
    }

}
