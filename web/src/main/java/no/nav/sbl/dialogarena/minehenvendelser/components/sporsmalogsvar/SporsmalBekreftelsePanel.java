package no.nav.sbl.dialogarena.minehenvendelser.components.sporsmalogsvar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Panel;

public class SporsmalBekreftelsePanel extends Panel {

    public SporsmalBekreftelsePanel(String id, final SideNavigerer nesteSide) {
        super(id);
        add(new AjaxLink<Void>("til-mine-henvendelser") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                nesteSide.forside();
                target.add(SporsmalBekreftelsePanel.this.getParent());
            }
        });
    }
}
