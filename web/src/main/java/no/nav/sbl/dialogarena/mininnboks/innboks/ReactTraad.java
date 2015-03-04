package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.ReactComponentPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.HashMap;
import java.util.Map;

public class ReactTraad extends BasePage {

    public ReactTraad(final PageParameters params) {
        final Map<String, Object> props = new HashMap<String, Object>() {{
            put("id", params.get("id").toString());
        }};

        add(new ReactComponentPanel("react", "Traadvisning", props));
    }
}
