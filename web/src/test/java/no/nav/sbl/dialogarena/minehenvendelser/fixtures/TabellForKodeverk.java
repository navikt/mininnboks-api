package no.nav.sbl.dialogarena.minehenvendelser.fixtures;

import no.nav.modig.test.fitnesse.fixture.ObjectPerRowFixture;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.KodeverkOppslag;
import no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects.Kodeverk;

public class TabellForKodeverk extends ObjectPerRowFixture<Kodeverk> {

    private KodeverkOppslag kodeverkOppslag;

    public TabellForKodeverk(KodeverkOppslag kodeverkOppslag) {
        this.kodeverkOppslag = kodeverkOppslag;
    }

    @Override
    protected void perRow(Row<Kodeverk> row) throws Exception {
        kodeverkOppslag.insertKodeverk(row.expected.kodeverkId, row.expected.navnPaaDokumentet);
    }

}
