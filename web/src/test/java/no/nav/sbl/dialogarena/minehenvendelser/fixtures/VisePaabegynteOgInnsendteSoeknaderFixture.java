package no.nav.sbl.dialogarena.minehenvendelser.fixtures;

import fit.Fixture;
import fitlibrary.ArrayFixture;
import no.nav.modig.core.context.SubjectHandlerUtils;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.core.domain.IdentType;
import no.nav.modig.test.fitnesse.fixture.SpringAwareDoFixture;
import no.nav.modig.test.fitnesse.fixture.ToDoList;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.minehenvendelser.config.FitNesseApplicationContext;
import no.nav.sbl.dialogarena.minehenvendelser.config.WicketApplication;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects.FitInnsendtBehandling;
import no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects.FitPaabegyntBehandling;
import no.nav.sbl.dialogarena.minehenvendelser.pages.HomePage;
import org.apache.wicket.Component;
import org.slf4j.Logger;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.setProperty;
import static no.nav.modig.core.context.SubjectHandler.SUBJECTHANDLER_KEY;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.apache.commons.lang3.StringUtils.join;
import static org.hamcrest.Matchers.equalTo;
import static org.slf4j.LoggerFactory.getLogger;

@ContextConfiguration(classes = {FitNesseApplicationContext.class})
@ActiveProfiles("test")
public class VisePaabegynteOgInnsendteSoeknaderFixture extends SpringAwareDoFixture {

    private static final Logger logger = getLogger(VisePaabegynteOgInnsendteSoeknaderFixture.class);
    @Inject
    private MockData mockData;
    @Inject
    private FluentWicketTester<WicketApplication> wicketTester;

    public Fixture datagrunnlag() {
        setProperty(SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getName());
        setProperty("no.nav.modig.security.systemuser.username", "BD01");
        logger.info("Setting up datagrunnlag.");
        mockData.clear();
        return new Datagrunnlag(mockData);
    }

    public Fixture innsendt(String aktoerId) {
        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(aktoerId, IdentType.EksternBruker).withAuthLevel(4).getSubject());
        wicketTester.goTo(HomePage.class);

        List<String> antallVedlegg = retrieveSubComponentsBasedOnFilterString("vedlegg");
        List<String> innsendtDato = retrieveSubComponentsBasedOnFilterString("innsendtDato");
        List<String> behandlingTittler = retrieveSubComponentsBasedOnFilterString("tittel");
        List<String> innsendteDokumenter = retrieveFerdigMangledeOrInnsendteDokumenter("innsendteDokumenter");
        List<String> manglendeDokumenter = retrieveFerdigMangledeOrInnsendteDokumenter("manglendeDokumenter");
        List<FitInnsendtBehandling> fitInnsendtBehandlinger = convertListToInnsendt(antallVedlegg, innsendtDato, behandlingTittler, innsendteDokumenter, manglendeDokumenter);

        return new ArrayFixture(fitInnsendtBehandlinger);
    }

    public Fixture paabegynt(String aktoerId) {
        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(aktoerId, IdentType.EksternBruker).withAuthLevel(4).getSubject());
        wicketTester.goTo(HomePage.class);
        return new ArrayFixture(retriveUnderArbeidBehandlinger());
    }

    public Fixture avklaringer() {
        return new ToDoList();
    }

    /**
     * Helper methods *
     */

    private List<FitPaabegyntBehandling> retriveUnderArbeidBehandlinger() {
        List<FitPaabegyntBehandling> fitPaabegyntBehandlinger = new ArrayList<>();
        List<Component> behandlingerUnderArbeid = wicketTester.get().components(withId("behandlingerUnderArbeid"));
        Component behandlingUnderArbeid = behandlingerUnderArbeid.get(0);

        //Henter ut antall på denne måten da listen med behandlingerUnderArbeid ikke alltid har riktig antall elementer
        int antallBehandlinger = wicketTester.get().components(withId("tittel").and(containedInComponent(equalTo(behandlingUnderArbeid)))).size();
        for (int i = 0; i < antallBehandlinger; i++) {
            fitPaabegyntBehandlinger.add(
                    new FitPaabegyntBehandling(
                            retrieveTekst("tittel", i, behandlingUnderArbeid),
                            retrieveTekst("sistEndret", i, behandlingUnderArbeid)));
        }
        return fitPaabegyntBehandlinger;
    }

    private String retrieveTekst(String id, int i, Component enclosingComponent) {
        List<Component> components = wicketTester.get().components(withId(id).and(containedInComponent(equalTo(enclosingComponent))));
        return components.get(i).getDefaultModelObjectAsString();
    }

    private List<String> retrieveFerdigMangledeOrInnsendteDokumenter(String filterString) {
        List<String> manglendeDokumenter = new ArrayList<>();
        for (Component component : wicketTester.get().components(withId(filterString))) {
            List<String> doktitler = new ArrayList<>();
            for (Component subComponent : wicketTester.get().components(containedInComponent(equalTo(component)).and(withId("dokument")))) {
                doktitler.add(subComponent.getDefaultModelObjectAsString());
            }
            manglendeDokumenter.add(join(doktitler, ", "));
        }
        return manglendeDokumenter;
    }

    private List<String> retrieveSubComponentsBasedOnFilterString(String filterString) {
        List<String> subComponents = new ArrayList<>();
        for (Component behandlingFerdig : wicketTester.get().components(withId("behandlingerFerdig"))) {
            for (Component component : wicketTester.get().components(withId(filterString).and(containedInComponent(equalTo(behandlingFerdig))))) {
                subComponents.add(component.getDefaultModelObjectAsString());
            }
        }
        return subComponents;
    }

    private List<FitInnsendtBehandling> convertListToInnsendt(List<String> antallVedlegg, List<String> innsendtDato, List<String> behandlingTittler, List<String> innsendteDokumenter, List<String> manglendeDokumenter) {
        List<FitInnsendtBehandling> fitInnsendtBehandlinger = new ArrayList<>();
        for (int i = 0; i < antallVedlegg.size(); i++) {
            fitInnsendtBehandlinger.add(
                    new FitInnsendtBehandling(
                            antallVedlegg.get(i),
                            innsendtDato.get(i),
                            behandlingTittler.get(i),
                            innsendteDokumenter.get(i),
                            manglendeDokumenter.get(i)));
        }
        return fitInnsendtBehandlinger;
    }
}
