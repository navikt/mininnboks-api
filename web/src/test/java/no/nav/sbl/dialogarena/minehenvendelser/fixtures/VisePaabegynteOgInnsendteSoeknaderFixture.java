package no.nav.sbl.dialogarena.minehenvendelser.fixtures;

import fit.Fixture;
import fitlibrary.ArrayFixture;
import no.nav.modig.test.fitnesse.fixture.SpringAwareDoFixture;
import no.nav.modig.test.fitnesse.fixture.ToDoList;
import no.nav.sbl.dialogarena.minehenvendelser.config.ApplicationContext;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects.FitInnsendtBehandling;
import no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects.FitPaabegyntBehandling;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.MockData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(classes = ApplicationContext.class)
@ActiveProfiles("stub")
public class VisePaabegynteOgInnsendteSoeknaderFixture extends SpringAwareDoFixture {

    private static final Logger logger = LoggerFactory.getLogger(VisePaabegynteOgInnsendteSoeknaderFixture.class);

    @Inject
    private MockData mockData;

    public Fixture datagrunnlag() {
        logger.info("Setting up datagrunnlag.");
        mockData.clearResponse();
        return new Datagrunnlag(mockData);
    }

    public Fixture innsendt(String aktoerId) {
        //TODO interact with application (i.e. wicket)
        List<Behandling> behandlinger = retrieveBehandlingsList(aktoerId);
        List<FitInnsendtBehandling> fitInnsendtBehandlinger = convertListToInnsendt(behandlinger);
        return new ArrayFixture(fitInnsendtBehandlinger);
    }

    public Fixture paabegynt(String aktoerId) {
        //TODO interact with application (i.e. wicket)
        List<Behandling> behandlinger = retrieveBehandlingsList(aktoerId);
        List<FitPaabegyntBehandling> fitInnsendtBehandlinger = convertListToPaabegynt(behandlinger);
        return new ArrayFixture(fitInnsendtBehandlinger);
    }

    public Fixture tabellForKodeverk() {
        return new TabellForKodeverk();
    }

    public Fixture avklaringer() {
        return new ToDoList();
    }

    private List<FitInnsendtBehandling> convertListToInnsendt(List<Behandling> behandlinger) {
        List<FitInnsendtBehandling> fitInnsendtBehandlinger = new ArrayList<>();
        for (Behandling behandling : behandlinger) {
            fitInnsendtBehandlinger.add(new FitInnsendtBehandling(behandling));
        }
        return fitInnsendtBehandlinger;
    }

    private List<FitPaabegyntBehandling> convertListToPaabegynt(List<Behandling> behandlinger) {
        List<FitPaabegyntBehandling> fitInnsendtBehandlinger = new ArrayList<>();
        for (Behandling behandling : behandlinger) {
            fitInnsendtBehandlinger.add(new FitPaabegyntBehandling(behandling));
        }
        return fitInnsendtBehandlinger;
    }

    private List<Behandling> retrieveBehandlingsList(String aktoerId) {
        logger.info("Entered WebService interaction method! AktoerId: " + aktoerId);
        //TODO call WS
        return new ArrayList<>();
    }

}
