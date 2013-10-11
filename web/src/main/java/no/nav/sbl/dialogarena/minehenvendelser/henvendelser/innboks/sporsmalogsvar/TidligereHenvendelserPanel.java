package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.sporsmalogsvar;


import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.HenvendelseVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

public class TidligereHenvendelserPanel extends Panel {
    public TidligereHenvendelserPanel(String id) {
        super(id);
        add(new PropertyListView<HenvendelseVM>("tidligereHenvendelser") {
            @Override
            protected void populateItem(final ListItem<HenvendelseVM> item) {
                item.add(new Label("langOpprettetDato"));
                item.add(new Label("overskrift", new StringResourceModel("overskrift.${henvendelse.type}", item.getModel())));
                item.add(new MultiLineLabel("henvendelse.fritekst"));
            }
        });
    }
}
