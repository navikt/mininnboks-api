package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.sporsmalogsvar;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.InnboksVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;


public class NyesteHenvendelsePanel extends Panel {
    public NyesteHenvendelsePanel(String id, CompoundPropertyModel<InnboksVM> modell) {
        super(id);
        add(new Label("overskrift", new StringResourceModel("overskrift.${nyesteHenvendelse.henvendelse.type}", modell)));
        add(new Label("nyesteHenvendelse.langOpprettetDato"));
        add(new Label("nyesteHenvendelse.lestDato"));
        add(new MultiLineLabel("nyesteHenvendelse.henvendelse.fritekst"));
    }
}
