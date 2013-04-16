package no.nav.sbl.dialogarena.minehenvendelser.fixtures;

import no.nav.modig.test.fitnesse.fixture.ObjectPerRowFixture;
import no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects.FitInnsendtBehandling;

public class Innsendt extends ObjectPerRowFixture<FitInnsendtBehandling> {

    private String aktoerId;

    public Innsendt(String aktoerId) {
        this.aktoerId = aktoerId;
    }

    @Override
    protected void perRow(Row<FitInnsendtBehandling> asserter) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
