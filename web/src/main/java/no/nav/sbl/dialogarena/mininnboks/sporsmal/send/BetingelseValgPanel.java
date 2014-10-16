package no.nav.sbl.dialogarena.mininnboks.sporsmal.send;

import no.nav.sbl.dialogarena.mininnboks.sporsmal.Sporsmal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class BetingelseValgPanel extends Panel {

    public BetingelseValgPanel(String id, IModel<Sporsmal> model) {
        super(id);
        setOutputMarkupId(true);

        PropertyModel<Boolean> vilkarAkseptertModel = new PropertyModel<>(model, "betingelserAkseptert");

        AjaxCheckBox checkbox = new AjaxCheckBox("betingelserCheckbox", vilkarAkseptertModel) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(this);
            }
        };
        add(checkbox.setOutputMarkupId(true));

        add(new AjaxLink<Void>("visBetingelser") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                target.appendJavaScript("$('.betingelser').dialog('open');");
            }
        });
    }
}
