package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.MeldingService;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class AlleMeldingerPanel extends Panel {

    public AlleMeldingerPanel(String id, final InnboksModell innboksModell, final MeldingService service) {
        super(id);
        setOutputMarkupId(true);

        add(new PropertyListView<MeldingVM>("nyesteHenvendelseITraad") {
            @Override
            protected void populateItem(final ListItem<MeldingVM> item) {
                item.add(new Label("avsender"));
                item.add(new Label("melding.overskrift"));
                item.add(new Label("opprettetDato"));
                item.add(new Label("melding.fritekst"));
                item.add(hasCssClassIf("valgt", innboksModell.erValgtMelding(item.getModelObject())));
                item.add(hasCssClassIf("lest", item.getModelObject().erLest()));
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        // Merk meldingen som valgt
                        innboksModell.getInnboksVM().setValgtMelding(item.getModelObject());
                        send(getPage(), Broadcast.DEPTH, Innboks.VALGT_MELDING);
                        // Merk meldingen som lest
                        if (!item.getModelObject().erLest().getObject()) {
                            service.merkMeldingSomLest(item.getModelObject().melding.id);
                            item.getModelObject().melding.markerSomLest();
                        }
                        // Oppdater visningen
                        innboksModell.alleMeldingerSkalSkjulesHvisLitenSkjerm.setObject(true);
                        target.add(AlleMeldingerPanel.this);
                    }
                });
            }
        });
    }
}
