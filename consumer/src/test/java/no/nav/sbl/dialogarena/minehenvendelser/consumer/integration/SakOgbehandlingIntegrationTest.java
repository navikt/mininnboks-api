package no.nav.sbl.dialogarena.minehenvendelser.consumer.integration;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.SakogbehandlingIntegrationTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.AKTOR_ID;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createFinnSakOgBehandlingskjedeListeResponse;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.populateFinnbehandlingKjedeList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SakogbehandlingIntegrationTestContext.class})
public class SakOgbehandlingIntegrationTest {

    @Inject
    private SakogbehandlingService service;

    @Inject
    private MockData mockData;

    @Before
    public void before() {
        mockData.getFinnData().addResponse(AKTOR_ID,
                createFinnSakOgBehandlingskjedeListeResponse(populateFinnbehandlingKjedeList()));
    }

    @After
    public void after() {
        mockData.getFinnData().clear();
    }

    @Test
    public void verifyNumberOfFerdigeSoeknader() {
        List<Soeknad> soeknadList = service.finnFerdigeSoeknader(AKTOR_ID);
        assertNotNull(soeknadList);
        assertThat(soeknadList.size(), equalTo(3));
    }

    @Test
    public void verifyNumberOfSoeknaderUnderArbeid() {
        List<Soeknad> soeknadList = service.finnSoeknaderUnderArbeid(AKTOR_ID);
        assertNotNull(soeknadList);
        assertThat(soeknadList.size(), equalTo(2));
    }

}
