package no.nav.sbl.dialogarena.mininnboks.sporsmal.kvittering;

import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.innboks.Innboks;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;

public class KvitteringPage extends BasePage {

    public KvitteringPage() {
        add(new ExternalLink("tilDittNav", System.getProperty("dittnav.link.url")));
        add(new BookmarkablePageLink<Void>("tilMineHenvendelser", Innboks.class));
        add(new EpostPanel("epostPanel"));
    }

}
