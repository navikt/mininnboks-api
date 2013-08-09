package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.SakogbehandlingTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SakogbehandlingTestContext.class})
public class SakogbehandlingServiceTest {

    @Inject
    private MockData mockdata;

    @Inject
    private SakogbehandlingService service;

    @Test
    public void shouldReturnSoeknaderUnderArbeid() {
        List<Soeknad> soeknadList = service.hentSoeknaderUnderArbeid("***REMOVED***");
        assertNotNull(soeknadList);
        assertThat(soeknadList.size(), equalTo(1));
    }


    @After
    public void clearData() {
        mockdata.getFinnData().clearResponse();
    }
}
