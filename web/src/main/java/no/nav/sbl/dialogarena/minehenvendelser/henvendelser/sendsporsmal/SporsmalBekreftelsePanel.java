package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.Innboks;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;

public class SporsmalBekreftelsePanel extends Panel {

    public SporsmalBekreftelsePanel(String id, final CompoundPropertyModel<Sporsmal> model) {
        super(id);
        add(new Label("tidspunkt", new Model<String>() {
        	@Override
        	public String getObject() {
                return Datoformat.langMedTid(model.getObject().innsendingsTidspunkt);
            }
        }));
        add(new BookmarkablePageLink<>("til-mine-henvendelser", Innboks.class));
    }
}
