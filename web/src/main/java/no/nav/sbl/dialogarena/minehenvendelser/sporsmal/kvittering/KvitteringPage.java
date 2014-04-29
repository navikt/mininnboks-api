package no.nav.sbl.dialogarena.minehenvendelser.sporsmal.kvittering;

import no.nav.sbl.dialogarena.minehenvendelser.BasePage;

public class KvitteringPage extends BasePage {

    public KvitteringPage() {
        add(new KvitteringPanel("kvittering"));
    }
}
