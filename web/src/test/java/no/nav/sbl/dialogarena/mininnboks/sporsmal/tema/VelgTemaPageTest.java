package no.nav.sbl.dialogarena.mininnboks.sporsmal.tema;

import no.nav.sbl.dialogarena.mininnboks.WicketPageTest;
import no.nav.sbl.dialogarena.mininnboks.innboks.Innboks;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.send.SkrivPage;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
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
        wicketTester.should().containComponent(ofType(Form.class).and(withId("temavalg")))
                .should().inComponent(withId("temavalg")).containComponent(ofType(AjaxSubmitLink.class).and(withId("fortsett")))
                .should().containComponent(ofType(Link.class).and(withId("avbryt")));
    }

    @Test
    public void testFortsettMedValgtTema() {
        wicketTester.inForm(withId("temavalg")).select("tema", 0).andReturn()
                .click().link(withId("fortsett")).should().beOn(SkrivPage.class);
    }

    @Test
    public void testFortsettUtenValgtTema() {
        wicketTester.click().link(withId("fortsett")).should().beOn(VelgTemaPage.class);
    }

    @Test
    public void testAvbrytLenke() {
        wicketTester.click().link(withId("avbryt")).should().beOn(Innboks.class);
    }

}
