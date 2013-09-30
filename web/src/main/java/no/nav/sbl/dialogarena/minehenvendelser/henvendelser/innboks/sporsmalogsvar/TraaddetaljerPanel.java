package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.sporsmalogsvar;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.InnboksVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;

public class TraaddetaljerPanel extends Panel {

    public TraaddetaljerPanel(String id, final CompoundPropertyModel<InnboksVM> model) {
        super(id);
        setOutputMarkupId(true);
        add(
                new Label("tema", new StringResourceModel("${nyesteHenvendelse.henvendelse.tema}", model)),
                new NyesteHenvendelsePanel("nyeste-henvendelse"),
                new TidligereHenvendelserPanel("tidligere-henvendelser")
        );
    }
}
