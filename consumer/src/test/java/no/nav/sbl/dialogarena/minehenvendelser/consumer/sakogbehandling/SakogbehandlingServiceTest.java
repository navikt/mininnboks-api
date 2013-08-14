package no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.SakogbehandlingMockTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.FinnSakOgBehandlingskjedeListeResponse;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.Temaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;

import javax.inject.Inject;
import java.util.List;

import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil.createDummyBehandlingkjede;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SakogbehandlingMockTestContext.class})
public class SakogbehandlingServiceTest {

    private static final String TEST_AKTOER_ID = "***REMOVED***";

    @Inject
    private SakogbehandlingService service;

    @Inject
    private MockData mockData;

    @Before
    public void before() {
        mockData.getFinnData().addResponse(TEST_AKTOER_ID,
                new FinnSakOgBehandlingskjedeListeResponse()
                        .withResponse(new no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeResponse()
                                .withSak(new Sak().withTema(new Temaer().withKodeverksRef("Tema"))
                                        .withBehandlingskjede(createDummyBehandlingkjede()))));
    }

    @After
    public void after() {
        mockData.getFinnData().clear();
    }

    @Test
    public void shouldReturnSoeknaderUnderArbeid() {
        List<Soeknad> soeknadList = service.finnSoeknaderUnderArbeid(TEST_AKTOER_ID);
        assertNotNull(soeknadList);
        assertThat(soeknadList.size(), equalTo(1));
    }
}
