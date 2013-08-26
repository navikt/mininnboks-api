package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.Innboks;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.joda.time.DateTime;

public class SporsmalBekreftelsePanel extends Panel {
	
    public SporsmalBekreftelsePanel(String id, final CompoundPropertyModel<Sporsmal> model) {
        super(id);
        add(new Label("tidspunkt", new Model<String>() {
        	@Override
        	public String getObject() {
        		DateTime tidspunkt = model.getObject().innsendingsTidspunkt;
        		return "kl " + tidspunkt.toString("hh:mm") + " den " + tidspunkt.toString("dd. MMMM YYYY", Session.get().getLocale());
        	}
        }));
        add(new Link("til-mine-henvendelser") {
            @Override
            public void onClick() {
                setResponsePage(Innboks.class);
            }
        });
    }
}
