package no.nav.sbl.dialogarena.minehenvendelser.innboks.sporsmalogsvar;


import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.minehenvendelser.innboks.HenvendelseVM;
import org.apache.wicket.markup.html.basic.Label;
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
                item.add(new Label("overskrift", new StringResourceModel("innboks.tidligere-henvendelse.overskrift.${henvendelse.type}", item.getModel())));
                item.add(new URLParsingMultiLineLabel("henvendelse.fritekst"));
            }
        });
    }
}
