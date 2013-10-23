package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.Innboks;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;

public class SporsmalBekreftelsePanel extends Panel {

    public SporsmalBekreftelsePanel(String id) {
        super(id);
        add(new BookmarkablePageLink<>("til-mine-henvendelser", Innboks.class));
    }
}
