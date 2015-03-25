package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.ReactComponentPanel;

import java.util.HashMap;
import java.util.Map;

public class Feilside extends BasePage {
    public Feilside() {
        Map<String, Object> props = new HashMap<String, Object>() {{
            put("melding", "Det har skjedd en feil...");
        }};
        add(new ReactComponentPanel("react", "Feilmelding", props));
    }
}
