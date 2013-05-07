package no.nav.sbl.dialogarena.minehenvendelser.fixtures;

import no.nav.modig.test.fitnesse.fixture.ObjectPerRowFixture;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkServiceMock;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkService;
import no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects.Kodeverk;

public class TabellForKodeverk extends ObjectPerRowFixture<Kodeverk> {

    private KodeverkService kodeverkService;

    public TabellForKodeverk(KodeverkService kodeverkService) {
        this.kodeverkService = kodeverkService;
    }

    @Override
    protected void perRow(Row<Kodeverk> row) throws Exception {
        ((KodeverkServiceMock) kodeverkService).insertKodeverk(row.expected.kodeverkId, row.expected.navnPaaDokumentet);
    }

}
