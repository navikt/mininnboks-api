package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.sporsmalogsvar;


import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.HenvendelseVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.Henvendelsetype.SPORSMAL;

public class TidligereHenvendelserPanel extends Panel {
    public TidligereHenvendelserPanel(String id) {
        super(id);
        add(new PropertyListView<HenvendelseVM>("tidligereHenvendelser") {
            @Override
            protected void populateItem(final ListItem<HenvendelseVM> item) {
                HenvendelseVM henvendelseVM = item.getModelObject();
                String key = henvendelseVM.avType(SPORSMAL) ? "sendte-du" : "sendte-nav";
                item.add(new Label("dato-og-avsender", henvendelseVM.getLangOpprettetDato() + " " + new StringResourceModel(key, this, null).getString()));
                item.add(new Label("henvendelse.overskrift"));
                item.add(new Label("henvendelse.fritekst"));
            }
        });
    }
}
