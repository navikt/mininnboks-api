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
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractRangeValidator;
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

        private SporsmalForm(String id, final CompoundPropertyModel<Sporsmal> model) {
            super(id, model);

            Label tema = new Label("tema", new StringResourceModel("${tema}", model));
            tema.setOutputMarkupId(true);

            Label hjelpetekst = new Label("hjelpetekst", new ResourceModel("still-sporsmal-hjelp"));

            TextArea<Object> fritekst = new TextArea<>("fritekst");
            fritekst.setRequired(true);
            fritekst.add(NewlineCorrectingStringValidator.maximumLength(FRITEKST_MAKS_LENGDE));

            final FeedbackPanel feedbackPanel = new FeedbackPanel("validering");
            feedbackPanel.setOutputMarkupId(true);

            Link<Void> avbryt = new Link<Void>("avbryt") {
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
                    henvendelseService.stillSporsmal(spsm.getFritekst(), spsm.getTema(), SubjectHandler.getSubjectHandler().getUid());
                    send(getPage(), Broadcast.BREADTH, Innboks.OPPDATER_HENVENDELSER);
                    sideNavigerer.neste();
                    target.add(SendSporsmalPanel.this.getParent());
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedbackPanel);
                }
            };

            add(tema, hjelpetekst, feedbackPanel, fritekst, avbryt, send);
        }

        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(SendSporsmalPanel.class, "textarea.js")));
        }
    }

    private static class NewlineCorrectingStringValidator extends AbstractRangeValidator<Integer, String> {

        public NewlineCorrectingStringValidator(Integer minimum, Integer maximum) {
            super(minimum, maximum);
        }

        @Override
        protected Integer getValue(IValidatable<String> validatable) {
            if (validatable.getValue().contains("\r\n")) {
                return getCorrectedStringLength(validatable);
            }
            return validatable.getValue().length();
        }

        private int getCorrectedStringLength(IValidatable<String> validatable) {
            return validatable.getValue().replace("\r", "").length();
        }

        public static NewlineCorrectingStringValidator maximumLength(int length) {
            return new NewlineCorrectingStringValidator(null, length);
        }
    }
}
