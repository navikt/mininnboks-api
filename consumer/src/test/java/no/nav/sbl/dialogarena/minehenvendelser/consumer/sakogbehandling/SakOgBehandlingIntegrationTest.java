package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.SakogbehandlingTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SakogbehandlingTestContext.class})
public class SakOgBehandlingIntegrationTest {

    @Inject
    private MockData mockdata;

    @Inject
    private SakogbehandlingService service;

    @Test
    public void shouldIntegrateWithSakOgBehandlnigViaWebService() {
        List<Soeknad> saksList = service.hentSaker("***REMOVED***");
        assertNotNull(saksList);
        assertThat(saksList.size(), equalTo(1));
    }

    @After
    public void clearData() {
        mockdata.clear();
    }
}
