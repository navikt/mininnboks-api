package no.nav.sbl.dialogarena.minehenvendelser.fixtures;

import fit.Fixture;
import fitlibrary.ArrayFixture;
import no.nav.modig.test.fitnesse.fixture.SpringAwareDoFixture;
import no.nav.modig.test.fitnesse.fixture.ToDoList;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.modig.wicket.test.internal.Parameters;
import no.nav.sbl.dialogarena.minehenvendelser.config.FitNesseApplicationContext;
import no.nav.sbl.dialogarena.minehenvendelser.config.WicketApplication;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.context.ConsumerTestContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects.FitInnsendtBehandling;
import no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects.FitPaabegyntBehandling;
import no.nav.sbl.dialogarena.minehenvendelser.pages.HomePage;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.wicket.test.matcher.ComponentMatchers.containedInComponent;
import static no.nav.modig.wicket.test.matcher.ComponentMatchers.withId;
import static org.hamcrest.Matchers.equalTo;

@ContextConfiguration(classes = {FitNesseApplicationContext.class, ConsumerTestContext.class})
@ActiveProfiles("stub")
public class VisePaabegynteOgInnsendteSoeknaderFixture extends SpringAwareDoFixture {

    private static final Logger logger = LoggerFactory.getLogger(VisePaabegynteOgInnsendteSoeknaderFixture.class);
    @Inject
    private MockData mockData;
    @Inject
    private FluentWicketTester<WicketApplication> wicketTester;

    public Fixture datagrunnlag() {
        logger.info("Setting up datagrunnlag.");
        mockData.clear();
        return new Datagrunnlag(mockData);
    }

    public Fixture innsendt(String aktoerId) {
        //TODO interact with application (i.e. wicket)
        Parameters parameters = new Parameters();
        parameters.pageParameters.add("aktorId", aktoerId);
        wicketTester.goTo(HomePage.class, parameters);

        List<String> antallVedlegg = retriveFerdigAntallVedlegg();
        List<String> innsendtDato = retriveFerdigInnsendtDato();
        List<String> behandlingTittler = retriveFerdigBehandlingTittel();
        List<String> innsendteDokumenter = retriveFerdigInnsendteDokumenter();
        List<String> manglendeDokumenter = retriveFerdigMangledeDokumenter();
        List<FitInnsendtBehandling> fitInnsendtBehandlinger = convertListToInnsendt(antallVedlegg, innsendtDato, behandlingTittler, innsendteDokumenter, manglendeDokumenter);
        return new ArrayFixture(fitInnsendtBehandlinger);
    }

    public Fixture paabegynt(String aktoerId) {
        Parameters parameters = new Parameters();
        parameters.pageParameters.add("aktorId", aktoerId);
        wicketTester.goTo(HomePage.class, parameters);
        List<String> behandlingTittler = retriveUnderArbeidBehandlingTittel();
        List<String> behandlingAntall = retriveUnderArbeidBehandlingAntall();
        List<String> behandlingSistEndret = retriveUnderArbeidBehandlingSistEndret();


        List<FitPaabegyntBehandling> fitInnsendtBehandlinger = convertListToPaabegynt(behandlingTittler, behandlingAntall, behandlingSistEndret);


        return new ArrayFixture(fitInnsendtBehandlinger);
    }

    private List<String> retriveUnderArbeidBehandlingSistEndret() {
        List<String> sistEndret = new ArrayList<>();
        for (Component component : wicketTester.get().components(withId("sistEndret"))) {
            sistEndret.add(component.getDefaultModelObjectAsString());
        }
        return sistEndret;
    }

    private List<String> retriveUnderArbeidBehandlingAntall() {
        List<String> antall = new ArrayList<>();
        for (Component component : wicketTester.get().components(withId("antall"))) {
            antall.add(component.getDefaultModelObjectAsString());
        }
        return antall;
    }

    private List<String> retriveUnderArbeidBehandlingTittel() {
        List<String> tittler = new ArrayList<>();
        for (Component component : wicketTester.get().components(withId("tittel"))) {
            tittler.add(component.getDefaultModelObjectAsString());
        }
        return tittler;
    }

    private List<String> retriveFerdigMangledeDokumenter() {
        List<String> manglendeDokumenter = new ArrayList<>();
        for (Component manglendeDokumenterComponent : wicketTester.get().components(withId("manglendeDokumenter"))) {
            List<Component> dokumenterComponent = wicketTester.get().components(containedInComponent(equalTo(manglendeDokumenterComponent)).and(withId("dokument")));
            List<String> doktitler = new ArrayList<>();
            for (Component component : dokumenterComponent) {
                doktitler.add(component.getDefaultModelObjectAsString());
            }
            manglendeDokumenter.add(StringUtils.join(doktitler, ","));
        }
        return manglendeDokumenter;
    }

    private List<String> retriveFerdigInnsendteDokumenter() {
        List<String> innsendteDokumenter = new ArrayList<>();
        for (Component innsendteDokumenterComponent : wicketTester.get().components(withId("innsendteDokumenter"))) {
            List<Component> dokumenterComponent = wicketTester.get().components(containedInComponent(equalTo(innsendteDokumenterComponent)).and(withId("dokument")));
            List<String> doktitler = new ArrayList<>();
            for (Component component : dokumenterComponent) {
                doktitler.add(component.getDefaultModelObjectAsString());
            }
            innsendteDokumenter.add(StringUtils.join(doktitler, ","));
        }
        return innsendteDokumenter;
    }

    private List<String> retriveFerdigBehandlingTittel() {
        List<String> tittler = new ArrayList<>();
        for (Component component : wicketTester.get().components(withId("tittel"))) {
            tittler.add(component.getDefaultModelObjectAsString());
        }
        return tittler;
    }

    private List<String> retriveFerdigInnsendtDato() {
        List<String> innSendtDato = new ArrayList<>();
        for (Component component : wicketTester.get().components(withId("innsendtDato"))) {
            innSendtDato.add(component.getDefaultModelObjectAsString());
        }
        return innSendtDato;
    }

    private List<String> retriveFerdigAntallVedlegg() {
        List<String> antallVedlegg = new ArrayList<>();
        for (Component component : wicketTester.get().components(withId("vedlegg"))) {
            antallVedlegg.add(component.getDefaultModelObjectAsString());
        }
        return antallVedlegg;
    }

    public Fixture tabellForKodeverk() {
        return new TabellForKodeverk();
    }

    public Fixture avklaringer() {
        return new ToDoList();
    }

    private List<FitInnsendtBehandling> convertListToInnsendt(List<String> antallVedlegg, List<String> innsendtDato, List<String> behandlingTittler, List<String> innsendteDokumenter, List<String> manglendeDokumenter) {
        List<FitInnsendtBehandling> fitInnsendtBehandlinger = new ArrayList<>();
        for (int i = 0; i < antallVedlegg.size(); i++) {
            fitInnsendtBehandlinger.add(new FitInnsendtBehandling(antallVedlegg.get(i), innsendtDato.get(i), behandlingTittler.get(i), innsendteDokumenter.get(i), manglendeDokumenter.get(i)));
        }
        return fitInnsendtBehandlinger;
    }

    private List<FitPaabegyntBehandling> convertListToPaabegynt(List<String> behandlingTittler, List<String> behandlingAntall, List<String> behandlingSistEndret) {
        List<FitPaabegyntBehandling> fitPaabegyntBehandlinger = new ArrayList<>();
        for (int i = 0; i < behandlingTittler.size(); i++) {
            String tittel = behandlingTittler.get(i);
            String antall = behandlingAntall.get(i);
            String dato = behandlingSistEndret.get(i);
            fitPaabegyntBehandlinger.add(new FitPaabegyntBehandling(tittel, antall, dato));
        }
        return fitPaabegyntBehandlinger;
    }

    private List<Behandling> retrieveBehandlingsList(String aktoerId) {
        logger.info("Entered WebService interaction method! AktoerId: " + aktoerId);
        //TODO hent info fra app
        return new ArrayList<>();
    }

}
