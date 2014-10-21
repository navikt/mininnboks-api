package no.nav.sbl.dialogarena.mininnboks.sporsmal.send;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.test.internal.Parameters;
import no.nav.sbl.dialogarena.mininnboks.WicketPageTest;
import no.nav.sbl.dialogarena.mininnboks.config.HenvendelseServiceMockContext;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.kvittering.KvitteringPage;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.TemagruppeDropdown;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.ofType;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.send.SkrivPage.IKKE_AKSEPTERT_FEILMELDING_PROPERTY;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.send.SkrivPage.UNDERLIGGENDE_FEIL_FEILMELDING_PROPERTY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(classes = {HenvendelseServiceMockContext.class})
public class SkrivPageTest extends WicketPageTest {

    @Inject
    private HenvendelseService henvendelseService;

    @Before
    public void setup() {
        wicketTester.goTo(SkrivPage.class, new Parameters().param("temagruppe", Temagruppe.ARBD.name()));
    }

    @Test
    public void lasterSkrivPageKomponenter() {
        wicketTester.should().containComponent(withId("sporsmalForm").and(ofType(Form.class)))
                .printComponentsTree()
                .should().inComponent(withId("sporsmalForm")).containComponent(withId("temagruppeDropdown").and(ofType(TemagruppeDropdown.class)))
                .should().inComponent(withId("sporsmalForm")).containComponent(withId("tekstfelt").and(ofType(EnhancedTextArea.class)))
                .should().inComponent(withId("sporsmalForm")).containComponent(withId("betingelseValg").and(ofType(BetingelseValgPanel.class)))
                .should().inComponent(withId("sporsmalForm")).containComponent(withId("send").and(ofType(AjaxSubmitLink.class)))
                .should().inComponent(withId("sporsmalForm")).containComponent(withId("avbryt").and(ofType(Link.class)));
    }

    @Test
    public void senderSubmitOgViserKvitteringPageVedAksepterteBetingelserOgTekst() {
        wicketTester.inForm(withId("sporsmalForm"))
                .toggleCheckbox(withId("betingelserCheckbox"))
                .write("tekstfelt:text", "Dette er en tekst.").andReturn()
                .click().link(withId("send"))
                .should().beOn(KvitteringPage.class);
    }

    @Test
    public void kallerStillSporsmaalDersomSkjemaetValidererVedSubmit() {
        wicketTester.inForm(withId("sporsmalForm"))
                .toggleCheckbox(withId("betingelserCheckbox"))
                .write("tekstfelt:text", "Dette er en tekst.").andReturn()
                .click().link(withId("send"));

        verify(henvendelseService).stillSporsmal(anyString(), any(Temagruppe.class), anyString());
    }

    @Test
    public void stopperSubmitMedAksepterteBetingelserMedTomTekst() {
        wicketTester.inForm(withId("sporsmalForm"))
                .toggleCheckbox(withId("betingelserCheckbox")).andReturn()
                .click().link(withId("send")).should().beOn(SkrivPage.class);
    }

    @Test
    public void stopperSubmitMedTekstMenIkkeAkseptertBetingelser() {
        wicketTester.inForm(withId("sporsmalForm"))
                .write("tekstfelt:text", "Dette er en tekst.").andReturn()
                .click().link(withId("send")).should().beOn(SkrivPage.class);
    }

    @Test
    public void girEgenFeilmeldingVedTekstMenIkkeAksepterteBetingelser() {
        wicketTester.inForm(withId("sporsmalForm"))
                .write("tekstfelt:text", "Dette er en tekst.").andReturn()
                .click().link(withId("send"));

        List<String> errorMessages = wicketTester.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
        assertThat(errorMessages, contains(wicketTester.get().component(ofType(SkrivPage.class)).getString(IKKE_AKSEPTERT_FEILMELDING_PROPERTY)));
    }

    @Test
    public void girEgenFeilmeldingDersomBaksystemetFeiler() {
        when(henvendelseService.stillSporsmal(anyString(), any(Temagruppe.class), anyString())).thenThrow(new RuntimeException());

        wicketTester.inForm(withId("sporsmalForm"))
                .toggleCheckbox(withId("betingelserCheckbox"))
                .write("tekstfelt:text", "Dette er en tekst.").andReturn()
                .click().link(withId("send"));

        List<String> errorMessages = wicketTester.get().errorMessages();
        assertThat(errorMessages.isEmpty(), is(false));
        assertThat(errorMessages, contains(wicketTester.get().component(ofType(SkrivPage.class)).getString(UNDERLIGGENDE_FEIL_FEILMELDING_PROPERTY)));
    }

}
