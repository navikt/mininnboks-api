package no.nav.sbl.dialogarena.minehenvendelser.sporsmal.kvittering;

import no.nav.sbl.dialogarena.minehenvendelser.innboks.Innboks;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;

public class KvitteringPanel extends Panel {

    public KvitteringPanel(String id) {
        super(id);
        add(new ExternalLink("til-inngangsporten", System.getProperty("inngangsporten.link.url")));
        add(new BookmarkablePageLink<Void>("til-mine-henvendelser", Innboks.class));
    }
}
