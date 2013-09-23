package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks.Innboks;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.validation.validator.StringValidator;
import org.joda.time.DateTime;

public class SendSporsmalPanel extends Panel {

    private SideNavigerer sideNavigerer;
    private HenvendelseService henvendelseService;

    public SendSporsmalPanel(String id, CompoundPropertyModel<Sporsmal> model, final SideNavigerer sideNavigerer, HenvendelseService henvendelseService) {
        super(id);
        this.sideNavigerer = sideNavigerer;
        this.henvendelseService = henvendelseService;
        add(new SporsmalForm("sporsmal-form", model));
    }

    private final class SporsmalForm extends Form<Sporsmal> {

        private static final int FRITEKST_MAKS_LENGDE = 1000;

        private SporsmalForm(String id, CompoundPropertyModel<Sporsmal> model) {
            super(id, model);

            final FeedbackPanel feedbackPanel = new FeedbackPanel("validering");
            feedbackPanel.setOutputMarkupId(true);

            Label hjelpetekst = new Label("hjelpetekst", new ResourceModel("still-sporsmal-hjelp"));

            TextArea<Object> fritekst = new TextArea<>("fritekst");
            fritekst.setRequired(true);
            fritekst.add(StringValidator.maximumLength(FRITEKST_MAKS_LENGDE));

            Link avbryt = new Link("avbryt") {
                @Override
                public void onClick() {
                    setResponsePage(Innboks.class);
                }
            };

            AjaxSubmitLink send = new AjaxSubmitLink("send") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Sporsmal spsm = getModelObject();
                    spsm.innsendingsTidspunkt = DateTime.now();
                    String overskrift = "Spørsmål om " + spsm.getTema();
                    henvendelseService.stillSporsmal(spsm.getFritekst(), overskrift, spsm.getTema(), SubjectHandler.getSubjectHandler().getUid());
                    send(getPage(), Broadcast.BREADTH, Innboks.OPPDATER_HENVENDELSER);
                    sideNavigerer.neste();
                    target.add(SendSporsmalPanel.this.getParent());
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedbackPanel);
                }
            };

            add(feedbackPanel, hjelpetekst, fritekst, avbryt, send);
        }

        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(SendSporsmalPanel.class, "textarea.js")));

        }
    }
}
