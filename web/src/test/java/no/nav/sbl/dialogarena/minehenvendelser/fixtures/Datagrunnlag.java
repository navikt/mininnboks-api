package no.nav.sbl.dialogarena.minehenvendelser.fixtures;

import no.nav.modig.test.fitnesse.fixture.ObjectPerRowFixture;
import no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects.FitBehandling;
import no.nav.sbl.dialogarena.minehenvendelser.wsmock.MockData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Datagrunnlag extends ObjectPerRowFixture<FitBehandling> {

    private static final Logger logger = LoggerFactory.getLogger(Datagrunnlag.class);

    private MockData mockData;

    public Datagrunnlag(MockData mockData) {
        this.mockData = mockData;
    }

    @Override
    protected void perRow(Row<FitBehandling> row) throws Exception {
        logger.info("entered perRow for datagrunnlag. Row object info: " + row.expected.toString());
        mockDataInteraction(row.expected);
    }

    private void mockDataInteraction(FitBehandling fitBehandling) {
        logger.info("entered mockdatainteraction!");
        mockData.addBehandlingToMockData(fitBehandling.asBehandling());
    }
}
