package no.nav.sbl.dialogarena.minehenvendelser.fixtures;

import fit.Fixture;
import no.nav.modig.test.fitnesse.fixture.SpringAwareDoFixture;
import no.nav.modig.test.fitnesse.fixture.ToDoList;
import no.nav.sbl.dialogarena.minehenvendelser.config.ApplicationContext;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.MockData;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;

@ContextConfiguration(classes = ApplicationContext.class)
@ActiveProfiles("stub")
public class VisePaabegynteOgInnsendteSoeknaderFixture extends SpringAwareDoFixture {

    @Inject
    private MockData mockData;

    public Fixture datagrunnlag() {
        return new Datagrunnlag(mockData);
    }

    public Fixture innsendt(String aktoerId) {
        return new Innsendt(aktoerId);
    }

    public Fixture paabegynt(String aktoerId) {
        return new Fixture();
    }

    public Fixture avklaringer() {
        return new ToDoList();
    }

}
