package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.sporsmalogsvar;

import org.apache.wicket.markup.html.panel.Panel;

public class TraaddetaljerPanel extends Panel {

    public TraaddetaljerPanel(String id) {
        super(id);
        setOutputMarkupId(true);
        add(new NyesteHenvendelsePanel("nyeste-henvendelse"), new TidligereHenvendelserPanel("tidligere-henvendelser"));
    }
}
