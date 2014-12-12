package no.nav.sbl.dialogarena.mininnboks.innboks.besvare;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.mininnboks.panels.EpostPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.sbl.dialogarena.mininnboks.utils.Event.HENVENDELSE_BESVART;

public class KvitteringPanel extends Panel {
    public KvitteringPanel(String id, IModel<BesvareMeldingPanel.Svar> model) {
        super(id);

        add(new URLParsingMultiLineLabel("fritekst", new PropertyModel<BesvareMeldingPanel.Svar>(model, "fritekst")));
        add(new EpostPanel("epostPanel"));
        add(new AjaxLink("lukkKvittering") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                send(getPage(), Broadcast.BREADTH, HENVENDELSE_BESVART);
            }
        });
    }
}
