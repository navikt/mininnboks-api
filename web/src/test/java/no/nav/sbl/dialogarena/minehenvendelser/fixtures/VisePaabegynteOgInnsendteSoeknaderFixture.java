package no.nav.sbl.dialogarena.minehenvendelser.fixtures;

import fit.Fixture;
import no.nav.modig.test.fitnesse.fixture.SpringAwareDoFixture;
import no.nav.sbl.dialogarena.minehenvendelser.config.FitNesseApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = FitNesseApplicationContext.class)
public class VisePaabegynteOgInnsendteSoeknaderFixture extends SpringAwareDoFixture {

    public Fixture datagrunnlag() {
        return new Datagrunnlag();
    }

}
