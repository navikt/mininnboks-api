package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.ReactComponentPanel;

import java.util.HashMap;
import java.util.Map;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class ReactInnboks extends BasePage {
    public ReactInnboks() {
        Map<String, Object> props = new HashMap<String, Object>() {{
            put("fnr", getSubjectHandler().getUid());
        }};
        add(new ReactComponentPanel("react", "Listevisning", props));
    }
}
