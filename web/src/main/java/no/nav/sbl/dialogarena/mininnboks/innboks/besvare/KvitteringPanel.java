package no.nav.sbl.dialogarena.mininnboks.innboks.besvare;

import no.nav.sbl.dialogarena.mininnboks.panels.EpostPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.sbl.dialogarena.mininnboks.utils.Event.HENVENDELSE_BESVART;

public class KvitteringPanel extends Panel {
    public KvitteringPanel(String id) {
        super(id);

        add(new EpostPanel("epostPanel"));
        add(new AjaxLink("lukkKvittering") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BREADTH, HENVENDELSE_BESVART);
            }
        });
    }
}
