package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.sbl.dialogarena.mininnboks.WicketPageTest;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.junit.Before;
import org.junit.Test;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

public class InnboksTest extends WicketPageTest {

    @Before
    public void setup() {
        wicketTester.goTo(Innboks.class);
    }

    @Test
    public void testInnboksKomponenter() {
        wicketTester.should().containComponent(ofType(ExternalLink.class).and(withId("skrivNy")))
                .should().containComponent(ofType(NyesteMeldingPanel.class))
                .should().containComponent(ofType(TidligereMeldingerPanel.class))
                .should().containComponent(ofType(AvsenderBilde.class));
    }

}
