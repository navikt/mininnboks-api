package no.nav.sbl.dialogarena.mininnboks.innboks.besvare;

import no.nav.sbl.dialogarena.mininnboks.WicketPageTest;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import no.nav.sbl.dialogarena.mininnboks.innboks.traader.TraadVM;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BesvareMeldingPanelTest extends WicketPageTest {

    public static final String ID = "id";
    public static final String EKSTERN_AKTOR = "eksternAktor";
    public static final String TILKNYTTET_ENHET = "tilknyttetEnhet";
    public static final String FRITEKST = "fritekst";
    public static final Temagruppe TEMAGRUPPE = Temagruppe.ARBD;

    private TraadVM traadVM;
    private Henvendelse henvendelse;
    private BesvareMeldingPanel besvareMeldingPanel;

    @Before
    public void setUp() {
        henvendelse = opprettHenvendelse();
        traadVM = new TraadVM(ID, Temagruppe.ARBD, new ArrayList<>(asList(henvendelse)));
        Component oppdaterbarKomponent = new WebMarkupContainer("containerSomSkalOppdateres");
        oppdaterbarKomponent.setOutputMarkupId(true);
        besvareMeldingPanel = new BesvareMeldingPanel("besvareMeldingPanel", traadVM, oppdaterbarKomponent);
    }

    private static Henvendelse opprettHenvendelse() {
        Henvendelse henvendelse = new Henvendelse(ID, TEMAGRUPPE);
        henvendelse.traadId = ID;
        henvendelse.type = Henvendelsetype.SPORSMAL_SKRIFTLIG;
        henvendelse.opprettet = DateTime.now();
        henvendelse.eksternAktor = EKSTERN_AKTOR;
        henvendelse.tilknyttetEnhet = TILKNYTTET_ENHET;
        return henvendelse;
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
        assertThat(svar.temagruppe, is(TEMAGRUPPE));
        assertThat(svar.traadId, is(ID));
        assertThat(svar.eksternAktor, is(EKSTERN_AKTOR));
        assertThat(svar.tilknyttetEnhet, is(TILKNYTTET_ENHET));
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
