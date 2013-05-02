package no.nav.sbl.dialogarena.minehenvendelser.fixtures;

import fit.Fixture;
import fitlibrary.ArrayFixture;
import no.nav.modig.test.fitnesse.fixture.SpringAwareDoFixture;
import no.nav.modig.test.fitnesse.fixture.ToDoList;
import no.nav.modig.wicket.test.FluentWicketTester;
import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdDummy;
import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdService;
import no.nav.sbl.dialogarena.minehenvendelser.config.FitNesseApplicationContext;
import no.nav.sbl.dialogarena.minehenvendelser.config.WicketApplication;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.MockData;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkService;
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

@ContextConfiguration(classes = {FitNesseApplicationContext.class})
@ActiveProfiles("test")
public class VisePaabegynteOgInnsendteSoeknaderFixture extends SpringAwareDoFixture {

    private static final Logger logger = LoggerFactory.getLogger(VisePaabegynteOgInnsendteSoeknaderFixture.class);
    @Inject
    private MockData mockData;
    @Inject
    private FluentWicketTester<WicketApplication> wicketTester;
    @Inject
    private KodeverkService kodeverkService;
    @Inject
    private AktoerIdService aktoerIdService;

    public Fixture datagrunnlag() {
        logger.info("Setting up datagrunnlag.");
        mockData.clear();
        return new Datagrunnlag(mockData);
    }

    public Fixture innsendt(String aktoerId) {
        ((AktoerIdDummy) aktoerIdService).setAktoerId(aktoerId);
        wicketTester.goTo(HomePage.class);

        List<String> antallVedlegg = retriveFerdigAntallVedlegg();
        List<String> innsendtDato = retriveFerdigInnsendtDato();
        List<String> behandlingTittler = retriveFerdigBehandlingTittel();
        List<String> innsendteDokumenter = retriveFerdigInnsendteDokumenter();
        List<String> manglendeDokumenter = retriveFerdigMangledeDokumenter();
        List<FitInnsendtBehandling> fitInnsendtBehandlinger = convertListToInnsendt(antallVedlegg, innsendtDato, behandlingTittler, innsendteDokumenter, manglendeDokumenter);

        return new ArrayFixture(fitInnsendtBehandlinger);
    }

    public Fixture paabegynt(String aktoerId) {
        ((AktoerIdDummy) aktoerIdService).setAktoerId(aktoerId);
        wicketTester.goTo(HomePage.class);
        List<FitPaabegyntBehandling> fitInnsendtBehandlinger = retriveUnderArbeidBehandlinger();
        return new ArrayFixture(fitInnsendtBehandlinger);
    }

    public Fixture tabellForKodeverk() {
        return new TabellForKodeverk(kodeverkService);
    }

    public Fixture avklaringer() {
        return new ToDoList();
    }

    private List<FitPaabegyntBehandling> retriveUnderArbeidBehandlinger() {
        List<FitPaabegyntBehandling> fitPaabegyntBehandlinger = new ArrayList<>();
        List<Component> behandlingerUnderArbeid = wicketTester.get().components(withId("behandlingerUnderArbeid"));
        Component behandlingUnderArbeid = behandlingerUnderArbeid.get(0);
        //Henter ut antall på denne måten da listen med behandlingerUnderArbeid ikke alltid har riktig antall elementer
        int antallBehandlinger = wicketTester.get().components(withId("tittel").and(containedInComponent(equalTo(behandlingUnderArbeid)))).size();
        for (int i = 0; i < antallBehandlinger; i++) {
            String tittel = retriveTekst("tittel", i, behandlingUnderArbeid);
            String antall = retriveTekst("antall", i, behandlingUnderArbeid);
            String sistEndret = retriveTekst("sistEndret", i, behandlingUnderArbeid);
            fitPaabegyntBehandlinger.add(new FitPaabegyntBehandling(tittel, antall, sistEndret));
        }
        return fitPaabegyntBehandlinger;
    }

    private String retriveTekst(String id, int i, Component enclosingComponent) {
        List<Component> components = wicketTester.get().components(withId(id).and(containedInComponent(equalTo(enclosingComponent))));
        Component component = components.get(i);
        return component.getDefaultModelObjectAsString();
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
        List<Component> behandlingerFerdig = wicketTester.get().components(withId("behandlingerFerdig"));
        for (Component behandlingFerdig : behandlingerFerdig) {
            for (Component component : wicketTester.get().components(withId("tittel").and(containedInComponent(equalTo(behandlingFerdig))))) {
                tittler.add(component.getDefaultModelObjectAsString());
            }
        }
        return tittler;
    }

    private List<String> retriveFerdigInnsendtDato() {
        List<String> innSendtDato = new ArrayList<>();
        List<Component> behandlingerFerdig = wicketTester.get().components(withId("behandlingerFerdig"));
        for (Component behandlingFerdig : behandlingerFerdig) {
            for (Component component : wicketTester.get().components(withId("innsendtDato").and(containedInComponent(equalTo(behandlingFerdig))))) {
                innSendtDato.add(component.getDefaultModelObjectAsString());
            }
        }
        return innSendtDato;
    }

    private List<String> retriveFerdigAntallVedlegg() {
        List<String> antallVedlegg = new ArrayList<>();
        List<Component> behandlingerFerdig = wicketTester.get().components(withId("behandlingerFerdig"));
        for (Component behandlingFerdig : behandlingerFerdig) {
            for (Component component : wicketTester.get().components(withId("vedlegg").and(containedInComponent(equalTo(behandlingFerdig))))) {
                antallVedlegg.add(component.getDefaultModelObjectAsString());
            }
        }
        return antallVedlegg;
    }

    private List<FitInnsendtBehandling> convertListToInnsendt(List<String> antallVedlegg, List<String> innsendtDato, List<String> behandlingTittler, List<String> innsendteDokumenter, List<String> manglendeDokumenter) {
        List<FitInnsendtBehandling> fitInnsendtBehandlinger = new ArrayList<>();
        for (int i = 0; i < antallVedlegg.size(); i++) {
            fitInnsendtBehandlinger.add(new FitInnsendtBehandling(antallVedlegg.get(i), innsendtDato.get(i), behandlingTittler.get(i), innsendteDokumenter.get(i), manglendeDokumenter.get(i)));
        }
        return fitInnsendtBehandlinger;
    }
}
