package no.nav.sbl.dialogarena.mininnboks.sporsmal.tema;

import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.innboks.Innboks;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.send.SkrivPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import static java.util.Arrays.asList;

public class VelgTemaPage extends BasePage {

    private final FeedbackPanel feedback;
    private Tema tema = null;

    public VelgTemaPage() {
        feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);
        add(new TemaValg("temavalg"));
        add(new Fortsettknapp("fortsett"));
        add(new Link<Void>("avbryt") {
            @Override
            public void onClick() {
                setResponsePage(Innboks.class);
            }
        });
    }


    private final class Fortsettknapp extends AjaxLink<Void> {

        public Fortsettknapp(String id) {
            super(id);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            if (tema != null) {
                setResponsePage(SkrivPage.class, new PageParameters().set("tema", tema.name()));
            } else {
                error(new StringResourceModel("send-sporsmal.temavelger.ikke-valgt-tema", VelgTemaPage.this, null).getString());
                target.add(feedback);
            }
        }

    }

    private final class TemaValg extends RadioGroup<Tema> {

        public TemaValg(String id) {
            super(id, new Model<Tema>());
            add(new ListView<Tema>("temaer", asList(Tema.values())) {
                @Override
                protected void populateItem(ListItem<Tema> item) {
                    item.add(
                            new Radio<>("valg", item.getModel()),
                            new Label("navn", new ResourceModel(item.getModelObject().toString())),
                            new Label("beskrivelse", new ResourceModel(item.getModelObject() + ".beskrivelse")));
                }
            });
            add(new AjaxFormChoiceComponentUpdatingBehavior() {
                @Override
                protected void onUpdate(AjaxRequestTarget target) {
                    tema = TemaValg.this.getModelObject();
                }
            });
        }

    }
}
