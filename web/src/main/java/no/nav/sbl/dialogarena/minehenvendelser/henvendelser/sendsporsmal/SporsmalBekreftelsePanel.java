package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.Innboks;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

public class SporsmalBekreftelsePanel extends Panel {

    public SporsmalBekreftelsePanel(String id) {
        super(id);
        add(new Link<Void>("til-mine-henvendelser") {
            @Override
            public void onClick() {
                setResponsePage(Innboks.class);
            }
        });
    }
}
