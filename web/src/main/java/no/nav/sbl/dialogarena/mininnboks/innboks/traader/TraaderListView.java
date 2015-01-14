package no.nav.sbl.dialogarena.mininnboks.innboks.traader;

import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.innboks.besvare.BesvareMeldingPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.StringValue;

import javax.inject.Inject;
import java.util.List;

import static java.lang.String.format;
import static no.nav.modig.wicket.conditional.ConditionalUtils.attributeIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.mininnboks.innboks.traader.TraadVM.erLest;
import static org.apache.wicket.AttributeModifier.append;

public class TraaderListView extends ListView<TraadVM> {

    @Inject
    private HenvendelseService service;

    private StringValue valgtId;
    private String valgtTraadWicketId;

    public TraaderListView(String id, IModel<List<TraadVM>> model, StringValue valgtId) {
        super(id, model);
        this.valgtId = valgtId;
    }

    @Override
    protected void populateItem(final ListItem<TraadVM> item) {
        final TraadVM traadVM = item.getModelObject();

        if (!valgtId.isEmpty() && traadVM.inneholderHenvendelseMedId(valgtId.toString())) {
            traadVM.lukket.setObject(false);
            valgtTraadWicketId = item.getMarkupId();
        }

        item.setOutputMarkupId(true);
        item.add(hasCssClassIf("lest", erLest(traadVM.henvendelser)));
        item.add(hasCssClassIf("closed", traadVM.lukket));
        item.add(new AjaxEventBehavior("click") {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                if (traadVM.lukket.getObject()) {
                    traadClickBehaviour(item, target);
                }
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                super.updateAjaxAttributes(attributes);
                attributes.setAllowDefault(true);
            }
        });

        WebMarkupContainer traadcontainer = new WebMarkupContainer("traadcontainer");

        AjaxLink<Void> flipp = new AjaxLink<Void>("flipp") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                traadClickBehaviour(item, target);
            }
        };
        Label ariahelper = new Label("ariahelper", traadVM.ariaTekst);
        flipp.add(attributeIf("aria-pressed", "true", not(traadVM.lukket), true));
        flipp.add(append("aria-controls", traadcontainer.getMarkupId()));
        flipp.add(append("aria-labelledby", ariahelper.getMarkupId()));
        flipp.add(ariahelper);

        traadcontainer.add(attributeIf("aria-expanded", "true", not(traadVM.lukket), true));
        traadcontainer.add(
                new BesvareMeldingPanel("besvareMelding", item.getModelObject(), item),
                new NyesteMeldingPanel("nyesteMelding", item.getModel()),
                new TidligereMeldingerPanel("tidligereMeldinger", item.getModel()));

        item.add(flipp, traadcontainer);
    }

    private void traadClickBehaviour(ListItem<TraadVM> item, AjaxRequestTarget target) {
        IModel<TraadVM> traadVM = item.getModel();

        if (!erLest(traadVM.getObject().henvendelser).getObject()) {
            traadVM.getObject().markerSomLest(service);
        }
        traadVM.getObject().lukket.setObject(!traadVM.getObject().lukket.getObject());
        target.add(item);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        if (!valgtId.isEmpty()) {
            response.render(OnLoadHeaderItem.forScript(format("window.location.hash = '%s'", valgtTraadWicketId)));
        }
    }
}
