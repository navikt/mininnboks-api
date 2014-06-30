package no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe;

import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.innboks.Innboks;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.send.SkrivPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import static java.util.Arrays.asList;

public class VelgTemagruppePage extends BasePage {

    private IModel<Temagruppe> temagruppe = new Model<>();

    public VelgTemagruppePage() {
        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);

        Form form = new Form<>("temagruppevalg");
        form.add(new RadioGroup<>("temagruppe", temagruppe)
                .setRequired(true)
                .add(new ListView<Temagruppe>("temagrupper", asList(Temagruppe.values())) {
                    @Override
                    protected void populateItem(ListItem<Temagruppe> item) {
                        item.add(
                                new Radio<>("valg", item.getModel()),
                                new Label("navn", new ResourceModel(item.getModelObject().toString())),
                                new Label("beskrivelse", new ResourceModel(item.getModelObject() + ".beskrivelse")));
                    }
                }));
        form.add(new AjaxSubmitLink("fortsett") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setResponsePage(SkrivPage.class, new PageParameters().set("temagruppe", temagruppe.getObject().name()));
            }
            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedback);
            }
        });
        form.add(feedback);

        add(form);
        add(new Link<Void>("avbryt") {
            @Override
            public void onClick() {
                setResponsePage(Innboks.class);
            }
        });
    }
}
