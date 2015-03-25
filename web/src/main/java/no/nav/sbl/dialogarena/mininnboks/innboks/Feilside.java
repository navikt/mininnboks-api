package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.ReactComponentPanel;

import java.util.HashMap;
import java.util.Map;

public class Feilside extends BasePage {
    public Feilside() {
        Map<String, Object> props = new HashMap<String, Object>() {{
            put("visIkon", true);
            put("melding", "Oops, noe gikk galt");
            put("brodtekst", "Siden eller tjenesten finnes ikke eller er for tiden utilgjengelig. Vi beklager dette. Prøv igjen senere.");
        }};
        add(new ReactComponentPanel("react", "Feilmelding", props));
    }
}
