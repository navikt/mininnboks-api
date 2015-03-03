package no.nav.sbl.dialogarena.mininnboks.innboks.besvare;

import no.nav.sbl.dialogarena.mininnboks.WicketPageTest;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;
import no.nav.sbl.dialogarena.mininnboks.innboks.traader.TraadVM;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static no.nav.sbl.dialogarena.mininnboks.TestUtils.lagForsteHenvendelseITraad;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BesvareMeldingPanelTest extends WicketPageTest {

    public static final String FRITEKST = "fritekst";

    private TraadVM traadVM;
    private Henvendelse henvendelse;
    private BesvareMeldingPanel besvareMeldingPanel;

    @Before
    public void setUp() {
        henvendelse = lagForsteHenvendelseITraad();
        traadVM = new TraadVM(henvendelse.traadId, henvendelse.temagruppe, new ArrayList<>(asList(henvendelse)));
        Component oppdaterbarKomponent = new WebMarkupContainer("containerSomSkalOppdateres");
        oppdaterbarKomponent.setOutputMarkupId(true);
        besvareMeldingPanel = new BesvareMeldingPanel("besvareMeldingPanel", traadVM, oppdaterbarKomponent);
    }

    @Test
    public void helePaneletErUsynligDersomTraadenErLukket() {
        wicketTester.goToPageWith(besvareMeldingPanel)
                .should().containComponent(thatIsInvisible().and(withId("besvareMeldingPanel")));
    }

    @Test
    public void alleKomponenterErSkjultNaarTraadenErAapenMenIkkeKanBesvares() {
        traadVM.lukket.setObject(false);

        wicketTester.goToPageWith(besvareMeldingPanel)
                .should().containComponent(thatIsInvisible().and(withId("besvareKnapp")))
                .should().containComponent(thatIsInvisible().and(withId("form")))
                .should().containComponent(thatIsInvisible().and(withId("kvittering")));
    }

    @Test
    public void kunBesvarKnappSynligNaarTraadenErAapenOgKanBesvares() {
        initierAapentBesvareMeldingPanel();

        wicketTester.goToPageWith(besvareMeldingPanel)
                .should().containComponent(thatIsVisible().and(withId("besvareKnapp")))
                .should().containComponent(thatIsInvisible().and(withId("form")))
                .should().containComponent(thatIsInvisible().and(withId("kvittering")));
    }

    @Test
    public void besvarKnappSkjulesOgFormVisesNaarManTrykkerBesvar() {
        initierAapentBesvareMeldingPanel();

        wicketTester.goToPageWith(besvareMeldingPanel)
                .click().link(withId("besvareKnapp"))
                .should().containComponent(thatIsInvisible().and(withId("besvareKnapp")))
                .should().containComponent(thatIsVisible().and(withId("form")))
                .should().containComponent(thatIsInvisible().and(withId("kvittering")));
    }

    @Test
    public void kunBesvarKnappErSynligNaarManAvbryter() {
        initierAapentBesvareMeldingPanel();

        wicketTester.goToPageWith(besvareMeldingPanel)
                .click().link(withId("besvareKnapp"))
                .click().link(withId("avbryt"))
                .should().containComponent(thatIsVisible().and(withId("besvareKnapp")))
                .should().containComponent(thatIsInvisible().and(withId("form")))
                .should().containComponent(thatIsInvisible().and(withId("kvittering")));
    }

    @Test
    public void kvitteringVisesNaarManBesvarer() {
        initierAapentBesvareMeldingPanel();

        aapneBesvareMeldingPanelOgBesvar();

        wicketTester
                .should().containComponent(thatIsInvisible().and(withId("besvareKnapp")))
                .should().containComponent(thatIsInvisible().and(withId("form")))
                .should().containComponent(thatIsVisible().and(withId("kvittering")));
    }

    @Test
    public void senderSvarTilHenvendelseServiceMedRiktigeFelter() {
        initierAapentBesvareMeldingPanel();

        aapneBesvareMeldingPanelOgBesvar();

        Henvendelse svar = traadVM.nyesteHenvendelse().getObject();
        assertThat(svar.fritekst, is(FRITEKST));
        assertThat(svar.temagruppe, is(henvendelse.temagruppe));
        assertThat(svar.traadId, is(henvendelse.id));
        assertThat(svar.eksternAktor, is(henvendelse.eksternAktor));
        assertThat(svar.tilknyttetEnhet, is(henvendelse.tilknyttetEnhet));
    }

    private void aapneBesvareMeldingPanelOgBesvar() {
        wicketTester.goToPageWith(besvareMeldingPanel)
                .click().link(withId("besvareKnapp"))
                .inForm("besvareMeldingPanel:form")
                .write("fritekst:text", FRITEKST)
                .submitWithAjaxButton(withId("sendSvar"));
    }

    private void initierAapentBesvareMeldingPanel() {
        traadVM.lukket.setObject(false);
        henvendelse.type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE;
    }

}
