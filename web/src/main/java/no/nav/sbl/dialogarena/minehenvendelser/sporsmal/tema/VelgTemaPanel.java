package no.nav.sbl.dialogarena.minehenvendelser.sporsmal.tema;

import no.nav.sbl.dialogarena.minehenvendelser.innboks.Innboks;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.SideNavigerer;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.Sporsmal;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import static java.util.Arrays.asList;

public class VelgTemaPanel extends Panel {

    IModel<Sporsmal> model;

    public VelgTemaPanel(String id, final IModel<Sporsmal> model, final SideNavigerer sideNavigerer) {
        super(id, model);

        this.model = model;

        WebMarkupContainer container = new WebMarkupContainer("tema-container");

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        container.add(feedback);

        container.add(getTemavalgListe("tema", feedback));
        container.add(getFortsettKnapp("fortsett", sideNavigerer, feedback));
        container.add(new Link<Void>("avbryt") {
            @Override
            public void onClick() {
                setResponsePage(Innboks.class);
            }
        });
        add(container);
    }

    private AjaxLink<Void> getFortsettKnapp(String id, final SideNavigerer sideNavigerer, final FeedbackPanel feedback) {
        return new AjaxLink<Void>(id) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (model.getObject().harTema()) {
                    sideNavigerer.neste();
                    target.add(VelgTemaPanel.this.getParent());
                } else {
                    error(new StringResourceModel("send-sporsmal.temavelger.ikke-valgt-tema", VelgTemaPanel.this, null).getString());
                    target.add(feedback);
                }
            }
        };
    }

    private RadioGroup<Tema> getTemavalgListe(String id, final FeedbackPanel feedback) {
        final RadioGroup<Tema> temaValg = new RadioGroup<>(id, new Model<Tema>());
        temaValg.add(new ListView<Tema>("temaliste", asList(Tema.values())) {
            @Override
            protected void populateItem(ListItem<Tema> item) {
                Radio<Tema> temavalg = new Radio<>("temavalg", item.getModel());
                item.add(temavalg);

                AttributeModifier bindLabelTilValg = new AttributeModifier("for", temavalg.getMarkupId());

                Label temanavn = new Label("temanavn",
                        new StringResourceModel(item.getModelObject().toString(), VelgTemaPanel.this, null));
                temanavn.add(bindLabelTilValg);
                item.add(temanavn);

                Label temabeskrivelse = new Label("temabeskrivelse",
                        new StringResourceModel(item.getModelObject().toString() + ".beskrivelse", VelgTemaPanel.this, null));
                temabeskrivelse.add(bindLabelTilValg);
                item.add(temabeskrivelse);
            }
        });
        temaValg.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                Sporsmal sos = model.getObject();
                sos.setTema(temaValg.getModelObject());
                model.setObject(sos);
                target.add(temaValg, feedback);
            }
        });
        return temaValg;
    }
}
