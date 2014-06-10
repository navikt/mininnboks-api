package no.nav.sbl.dialogarena.mininnboks.sporsmal.tema;

import no.nav.sbl.dialogarena.mininnboks.WicketPageTest;
import no.nav.sbl.dialogarena.mininnboks.innboks.Innboks;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.Link;
import org.junit.Before;
import org.junit.Test;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

public class VelgTemaPageTest extends WicketPageTest {

    @Before
    public void setup() {
        wicketTester.goTo(VelgTemaPage.class);
    }

    @Test
    public void testVelgTemaKomponenter() {
        wicketTester.should().containComponent(ofType(RadioGroup.class).and(withId("temavalg")))
                .should().containComponent(ofType(AjaxLink.class).and(withId("fortsett")))
                .should().containComponent(ofType(Link.class).and(withId("avbryt")));
    }

    @Test
    public void testAvbrytLenke() {
        wicketTester.click().link(withId("avbryt")).should().beOn(Innboks.class);
    }

}
