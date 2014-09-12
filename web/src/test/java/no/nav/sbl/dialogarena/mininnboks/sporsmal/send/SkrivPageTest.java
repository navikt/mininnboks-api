package no.nav.sbl.dialogarena.mininnboks.sporsmal.send;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.test.internal.Parameters;
import no.nav.sbl.dialogarena.mininnboks.WicketPageTest;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.kvittering.KvitteringPage;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListView;
import org.junit.Before;
import org.junit.Test;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;

public class SkrivPageTest extends WicketPageTest {

    @Before
    public void setup() {
        wicketTester.goTo(SkrivPage.class, new Parameters().param("temagruppe", Temagruppe.ARBD.name()));
    }

    @Test
    public void skrivPageKomponenter() {
        wicketTester.should().containComponent(withId("sporsmal-form").and(ofType(Form.class)))
                .should().inComponent(withId("sporsmal-form")).containComponent(withId("temagruppe").and(ofType(Label.class)))
                .should().inComponent(withId("sporsmal-form")).containComponent(withId("temagruppe-liste").and(ofType(ListView.class)))
                .should().inComponent(withId("sporsmal-form")).containComponent(withId("tekstfelt").and(ofType(EnhancedTextArea.class)))
                .should().inComponent(withId("sporsmal-form")).containComponent(withId("betingelseValg").and(ofType(BetingelseValgPanel.class)))
                .should().inComponent(withId("sporsmal-form")).containComponent(withId("send").and(ofType(AjaxSubmitLink.class)))
                .should().inComponent(withId("sporsmal-form")).containComponent(withId("avbryt").and(ofType(Link.class)));
    }

    @Test
    public void sporsmalsinnsendingTomTekst() {
        wicketTester.inForm(withId("sporsmal-form"))
                .toggleCheckbox(withId("betingelserCheckbox")).andReturn()
                .click().link(withId("send")).should().beOn(SkrivPage.class);
    }

    @Test
    public void sporsmalsinnsendingMedTekstAkseptertBetingelser() {
        wicketTester.inForm(withId("sporsmal-form"))
                .toggleCheckbox(withId("betingelserCheckbox"))
                .write("tekstfelt:text", "Dette er en tekst.").andReturn()
                .click().link(withId("send"))
                .should().beOn(KvitteringPage.class);
    }

    @Test
    public void sporsmalsinnsendingMedTekstIkkeAkseptertBetingelser() {
        wicketTester.inForm(withId("sporsmal-form"))
                .write("tekstfelt:text", "Dette er en tekst.").andReturn()
                .click().link(withId("send")).should().beOn(SkrivPage.class);
    }
}
