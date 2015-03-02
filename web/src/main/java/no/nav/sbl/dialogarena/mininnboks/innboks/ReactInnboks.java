package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.ReactComponentPanel;

public class ReactInnboks extends BasePage {
    public ReactInnboks() {
        add(new ReactComponentPanel("react", "Listevisning"));
    }
}
