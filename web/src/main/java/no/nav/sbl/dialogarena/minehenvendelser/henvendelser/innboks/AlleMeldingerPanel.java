package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class AlleMeldingerPanel extends Panel {

    public AlleMeldingerPanel(String id, final InnboksModell innboksModell) {
        super(id);
        setOutputMarkupId(true);

        add(new PropertyListView<MeldingVM>("meldinger") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {
                item.add(new MeldingsHeader("header"));
                item.add(new Label("fritekst"));
                item.add(hasCssClassIf("valgt", innboksModell.erValgtMelding(item.getModelObject())));
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        innboksModell.getInnboksVM().setValgtMelding(item.getModelObject());
                        send(getPage(), Broadcast.DEPTH, Innboks.VALGT_MELDING);
                        target.add(getParent());
                    }
                });
            }
        });
    }
}
