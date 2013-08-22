package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.melding;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

public class MeldingsHeader extends Panel {

    public MeldingsHeader(String id) {
        super(id);
        add(new Label("overskrift"));
        add(new Label("opprettetDato"));
    }
}
