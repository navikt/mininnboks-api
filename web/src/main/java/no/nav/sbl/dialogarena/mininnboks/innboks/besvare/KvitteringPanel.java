package no.nav.sbl.dialogarena.mininnboks.innboks.besvare;

import no.nav.sbl.dialogarena.mininnboks.panels.EpostPanel;
import org.apache.wicket.markup.html.panel.Panel;

public class KvitteringPanel extends Panel {
    public KvitteringPanel(String id) {
        super(id);

        add(new EpostPanel("epostPanel"));
    }
}
