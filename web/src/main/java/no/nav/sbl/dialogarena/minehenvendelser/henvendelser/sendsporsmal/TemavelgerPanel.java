package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.Innboks;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.value.IValueMap;
import org.apache.wicket.util.value.ValueMap;

import java.util.List;

public class TemavelgerPanel extends Panel {

    IModel<Sporsmal> model;

    public TemavelgerPanel(String id, final List<Tema> alleTema, final IModel<Sporsmal> model, final SideNavigerer sideNavigerer) {
        super(id, model);

        this.model = model;

        WebMarkupContainer container = new WebMarkupContainer("tema-container");

        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        container.add(feedback);

        container.add(getTemavalgListe("tema", alleTema, feedback));
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
                    target.add(TemavelgerPanel.this.getParent());
                } else {
                    error(new StringResourceModel("temavelger.ikkeValgtTema", this, null).getString());
                    target.add(feedback);
                }
            }
        };
    }

    private RadioChoice<Tema> getTemavalgListe(String id, List<Tema> alleTema, final FeedbackPanel feedback) {
        final RadioChoice<Tema> temaValg = new RadioChoice<>(id, alleTema, new EnumChoiceRenderer<Tema>(TemavelgerPanel.this) {
            @Override
            protected String resourceKey(Tema tema) {
                return tema.toString();
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
        setOutputMarkupId(true);
        return temaValg;
    }
}
