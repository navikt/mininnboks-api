package no.nav.sbl.dialogarena.minehenvendelser.pages.sakogbehandling;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.util.MockCreationUtil;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import no.nav.sbl.dialogarena.minehenvendelser.pages.AbstractWicketTest;
import org.junit.Test;

import java.util.Map;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

public class SoeknadPageTest extends AbstractWicketTest {

    @Override
    protected void setup() {
        mock("footerLinks", Map.class);
        mock("navigasjonslink", "");
        setupFakeCms();
    }

    @Test
    public void renderSoeknadPageWithBehandlingstidComponent() {
        SoeknadPage soeknadPage = new SoeknadPage(Soeknad.transformToSoeknad(MockCreationUtil.createDummyBehandlingkjede()));
        wicketTester.goTo(soeknadPage)
                .should().containComponent(withId("behandlingstid"));
    }

}
