package no.nav.sbl.dialogarena.minehenvendelser.fixtures;

import no.nav.modig.test.fitnesse.fixture.ObjectPerRowFixture;
import no.nav.sbl.dialogarena.minehenvendelser.fitnesseobjects.Kodeverk;

import java.util.HashMap;
import java.util.Map;

public class TabellForKodeverk extends ObjectPerRowFixture<Kodeverk> {

    private Map<String, String> kodeverkMap = new HashMap<>();

    public Map<String, String> getKodeverkMap() {
        return kodeverkMap;
    }

    @Override
    protected void perRow(Row<Kodeverk> row) throws Exception {
        kodeverkMap.put(row.expected.kodeverkId, row.expected.navnPaaDokumentet);
    }

}
