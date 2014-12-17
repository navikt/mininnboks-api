package no.nav.sbl.dialogarena.mininnboks.innboks.besvare;

import no.nav.sbl.dialogarena.mininnboks.WicketPageTest;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*;
import no.nav.sbl.dialogarena.mininnboks.innboks.traader.TraadVM;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Bean;

import javax.inject.Inject;
import java.util.ArrayList;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsInvisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.thatIsVisible;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class BesvareMeldingPanelTest extends WicketPageTest {

    public static final String ID = "id";
    public static final String EKSTERN_AKTOR = "eksternAktor";
    public static final String TILKNYTTET_ENHET = "tilknyttetEnhet";
    public static final String FRITEKST = "fritekst";
    public static final Temagruppe TEMAGRUPPE = Temagruppe.ARBD;

    @Bean
    public HenvendelseService henvendelseService() {
        return mock(HenvendelseService.class);
    }

    @Inject
    private HenvendelseService henvendelseService;

    private TraadVM traadVM;
    private Henvendelse henvendelse;
    private Component oppdaterbarKomponent;
    private BesvareMeldingPanel besvareMeldingPanel;

    @Before
    public void setUp() {
        henvendelse = opprettHenvendelse();
        traadVM = new TraadVM(ID, Temagruppe.ARBD, new ArrayList<>(asList(henvendelse)));
        oppdaterbarKomponent = new WebMarkupContainer("containerSomSkalOppdateres");
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
    public void alleKomponenterErUsynligeDersomTraadenErAapenMenIkkeKanBesvares() {
        traadVM.lukket.setObject(false);

        wicketTester.goToPageWith(besvareMeldingPanel)
                .should().containComponent(thatIsInvisible().and(withId("besvareKnapp")))
                .should().containComponent(thatIsInvisible().and(withId("besvareContainer")))
                .should().containComponent(thatIsInvisible().and(withId("kvittering")));
    }

    @Test
    public void kunBesvarKnappErSynligDersomTraadenErAapenOgKanBesvares() {
        initierAapentBesvareMeldingPanel();

        wicketTester.goToPageWith(besvareMeldingPanel)
                .should().containComponent(thatIsVisible().and(withId("besvareKnapp")))
                .should().containComponent(thatIsInvisible().and(withId("besvareContainer")))
                .should().containComponent(thatIsInvisible().and(withId("kvittering")));
    }

    @Test
    public void besvarKnappBlirUsynligOgBesvarContainerVisesDersomManTrykkerBesvar() {
        initierAapentBesvareMeldingPanel();

        wicketTester.goToPageWith(besvareMeldingPanel)
                .click().link(withId("besvareKnapp"))
                .should().containComponent(thatIsInvisible().and(withId("besvareKnapp")))
                .should().containComponent(thatIsVisible().and(withId("besvareContainer")))
                .should().containComponent(thatIsInvisible().and(withId("kvittering")));
    }

    @Test
    public void kunBesvarKnappErSynligDersomManAvbryterBesvarelse() {
        initierAapentBesvareMeldingPanel();

        wicketTester.goToPageWith(besvareMeldingPanel)
                .click().link(withId("besvareKnapp"))
                .click().link(withId("avbryt"))
                .should().containComponent(thatIsVisible().and(withId("besvareKnapp")))
                .should().containComponent(thatIsInvisible().and(withId("besvareContainer")))
                .should().containComponent(thatIsInvisible().and(withId("kvittering")));
    }

    @Test
    public void besvarKnappOgBesvarContainerBlirUsynligOgKvitteringVisesDersomManBesvarer() {
        initierAapentBesvareMeldingPanel();

        aapneBesvareMeldingPanelOgBesvar();

        wicketTester
                .should().containComponent(thatIsInvisible().and(withId("besvareKnapp")))
                .should().containComponent(thatIsInvisible().and(withId("besvareContainer")))
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
                .inForm("besvareMeldingPanel:besvareContainer:form")
                .write("fritekst:text", FRITEKST)
                .submitWithAjaxButton(withId("sendSvar"));
    }

    private void initierAapentBesvareMeldingPanel() {
        traadVM.lukket.setObject(false);
        henvendelse.type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE;
    }

}
