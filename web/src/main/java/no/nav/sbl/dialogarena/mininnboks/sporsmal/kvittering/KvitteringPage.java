package no.nav.sbl.dialogarena.mininnboks.sporsmal.kvittering;

import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.panels.EpostPanel;
import org.apache.wicket.markup.html.link.ExternalLink;

public class KvitteringPage extends BasePage {

    public KvitteringPage() {
        add(new ExternalLink("tilDittNav", System.getProperty("dittnav.link.url")));
        add(new ExternalLink("tilMinInnboks", "/mininnboks"));
        add(new EpostPanel("epostPanel"));
    }

}
