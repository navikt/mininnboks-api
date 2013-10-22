package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class SamtykkePanel extends Panel {

    public SamtykkePanel(String id, final SideNavigerer sideNavigerer) {
        super(id);

        add(new AjaxLink("neste") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                sideNavigerer.neste();
                target.add(SamtykkePanel.this.getParent());
            }
        });
    }
}
