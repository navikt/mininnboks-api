package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.modig.wicket.test.internal.Parameters;
import no.nav.sbl.dialogarena.mininnboks.WicketPageTest;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class InnboksTest extends WicketPageTest {

    @Inject
    private HenvendelseService henvendelseService;

    private List<Henvendelse> henvendelser;

    @Before
    public void setUp() {
        henvendelser = henvendelseService.hentAlleHenvendelser("");
    }

    @Test
    public void testInnboksKomponenter() {
        wicketTester.goTo(Innboks.class)
                .should().containComponent(ofType(ExternalLink.class).and(withId("skrivNy")))
                .should().containComponent(ofType(NyesteMeldingPanel.class))
                .should().containComponent(ofType(TidligereMeldingerPanel.class))
                .should().containComponent(ofType(AvsenderBilde.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void aapnerTraadMedIdFraPageParameter() {
        String valgtTraadId = henvendelser.get(1).traadId;

        wicketTester.goTo(Innboks.class, new Parameters().param("id", valgtTraadId));

        int antallTraader = ((List<TraadVM>) wicketTester.tester.getComponentFromLastRenderedPage("traader").getDefaultModelObject()).size();
        for (int i = 0; i < antallTraader; i++) {
            TraadVM traadVM = (TraadVM) wicketTester.tester.getComponentFromLastRenderedPage("traader:" + i).getDefaultModelObject();
            if (valgtTraadId.equals(traadVM.id)) {
                assertThat(traadVM.lukket.getObject(), is(false));
            } else {
                assertThat(traadVM.lukket.getObject(), is(true));
            }
        }
    }


}
