package no.nav.sbl.dialogarena.mininnboks.sporsmal.kvittering;

import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.innboks.Innboks;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;

public class KvitteringPage extends BasePage {

    public KvitteringPage() {
        add(new ExternalLink("til-inngangsporten", System.getProperty("inngangsporten.link.url")));
        add(new BookmarkablePageLink<Void>("til-mine-henvendelser", Innboks.class));
    }
}
