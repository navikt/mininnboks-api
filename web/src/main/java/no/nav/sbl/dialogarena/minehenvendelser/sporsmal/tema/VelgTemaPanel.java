package no.nav.sbl.dialogarena.minehenvendelser.sporsmal.tema;

import no.nav.sbl.dialogarena.minehenvendelser.innboks.Innboks;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.Sporsmal;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.Stegnavigator;
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
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

import static java.util.Arrays.asList;

public class VelgTemaPanel extends GenericPanel<Sporsmal> {

    private final Stegnavigator stegnavigator;
    private final FeedbackPanel feedback;

    public VelgTemaPanel(String id, IModel<Sporsmal> model, Stegnavigator stegnavigator) {
        super(id, model);
        this.setModel(model);
        this.stegnavigator = stegnavigator;
        this.feedback = new FeedbackPanel("feedback");
        this.feedback.setOutputMarkupId(true);

        add(
            feedback,
            new TemaValg("temavalg"),
            new Fortsettknapp("fortsett"),
            new Link<Void>("avbryt") {
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
            if (VelgTemaPanel.this.getModelObject().harTema()) {
                stegnavigator.neste();
                target.add(VelgTemaPanel.this.getParent());
            } else {
                error(new StringResourceModel("send-sporsmal.temavelger.ikke-valgt-tema", VelgTemaPanel.this, null).getString());
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
                    VelgTemaPanel.this.getModelObject().setTema(TemaValg.this.getModelObject());
                }
            });
        }

    }
}
