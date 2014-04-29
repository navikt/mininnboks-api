package no.nav.sbl.dialogarena.minehenvendelser.innboks.sporsmalogsvar;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.minehenvendelser.innboks.InnboksVM;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;


public class NyesteHenvendelsePanel extends Panel {
    public NyesteHenvendelsePanel(String id, CompoundPropertyModel<InnboksVM> modell) {
        super(id);
        add(new Label("overskrift", new StringResourceModel("innboks.nyeste-henvendelse.overskrift.${nyesteHenvendelse.henvendelse.type}", modell)));
        add(new Label("nyesteHenvendelse.langOpprettetDato"));
        add(new Label("nyesteHenvendelse.lestDato"));
        add(new URLParsingMultiLineLabel("nyesteHenvendelse.henvendelse.fritekst"));
    }
}
