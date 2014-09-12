package no.nav.sbl.dialogarena.mininnboks.sporsmal.send;

import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.modig.wicket.events.annotations.RefreshOnEvents;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.Sporsmal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import static no.nav.sbl.dialogarena.mininnboks.sporsmal.send.BetingelsePanel.BETINGELSER_BESVART;

@RefreshOnEvents(BETINGELSER_BESVART)
public class BetingelseValgPanel extends Panel {

    private final AjaxCheckBox checkbox;

    public BetingelseValgPanel(String id, IModel<Sporsmal> model) {
        super(id);
        setOutputMarkupId(true);

        PropertyModel<Boolean> vilkarAkseptertModel = new PropertyModel<>(model, "betingelserAkseptert");

        checkbox = new AjaxCheckBox("betingelserCheckbox", vilkarAkseptertModel) {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(this);
            }
        };
        add(checkbox.setOutputMarkupId(true));

        final ModigModalWindow vilkar = new ModigModalWindow("betingelser");
        vilkar.setInitialWidth(700);
        vilkar.setInitialHeight(750);
        vilkar.setContent(new BetingelsePanel(vilkar.getContentId(), vilkarAkseptertModel, vilkar));
        add(vilkar);

        add(new AjaxLink<Void>("visBetingelser") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                vilkar.show(target);
            }
        });
    }

    @RunOnEvents(BETINGELSER_BESVART)
    public void oppdaterCheckbox(AjaxRequestTarget target) {
        checkbox.modelChanged();
        target.add(checkbox);
    }

}
