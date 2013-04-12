package no.nav.sbl.dialogarena.minehenvendelser.fixtures;

import no.nav.modig.test.fitnesse.fixture.ObjectPerRowFixture;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects.FitBehandling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Datagrunnlag extends ObjectPerRowFixture<FitBehandling> {

    private static final Logger logger = LoggerFactory.getLogger(Datagrunnlag.class);

    private List<Behandling> behandlinger = new ArrayList<>();

    @Override
    protected void perRow(Row<FitBehandling> row) throws Exception {
        logger.info("entered perRow for datagrunnlag. Row object info: " + row.expected.toString());
        behandlinger.add(row.expected.asBehandling());
    }
}
