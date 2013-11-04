package no.nav.sbl.dialogarena.minehenvendelser.sporsmal.send;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.minehenvendelser.innboks.Innboks;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.Stegnavigator;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.Sporsmal;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractRangeValidator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendPanel extends Panel {

    private Stegnavigator stegnavigator;
    private HenvendelseService henvendelseService;
    private static final Logger LOG = LoggerFactory.getLogger(SendPanel.class);

    public SendPanel(String id, CompoundPropertyModel<Sporsmal> model, final Stegnavigator stegnavigator, HenvendelseService henvendelseService) {
        super(id);
        this.stegnavigator = stegnavigator;
        this.henvendelseService = henvendelseService;

        Label tema = new Label("tema", new StringResourceModel("${tema}", model));
        tema.setOutputMarkupId(true);

        add(tema, new SporsmalForm("sporsmal-form", model));
    }

    private final class SporsmalForm extends Form<Sporsmal> {

        private static final int FRITEKST_MAKS_LENGDE = 1000;

        private SporsmalForm(String id, final CompoundPropertyModel<Sporsmal> model) {
            super(id, model);

            TextArea<Object> fritekst = new TextArea<>("fritekst");
            fritekst.setRequired(true);
            fritekst.add(NewlineCorrectingStringValidator.maximumLength(FRITEKST_MAKS_LENGDE));

            final FeedbackPanel feedbackPanel = new FeedbackPanel("validering");
            feedbackPanel.setOutputMarkupId(true);

            AjaxSubmitLink send = new AjaxSubmitLink("send") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    try {
                        Sporsmal spsm = getModelObject();
                        spsm.innsendingsTidspunkt = DateTime.now();
                        henvendelseService.stillSporsmal(spsm.getFritekst(), spsm.getTema(), SubjectHandler.getSubjectHandler().getUid());
                        send(getPage(), Broadcast.BREADTH, Innboks.OPPDATER_HENVENDELSER);
                        stegnavigator.neste();
                        target.add(SendPanel.this.getParent());
                    } catch (Exception e) {
                        LOG.error("Feil ved innsending av spørsmål", e);
                        error(new StringResourceModel("send-sporsmal.still-sporsmal.underliggende-feil", SendPanel.this, null).getString());
                        target.add(feedbackPanel);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedbackPanel);
                }
            };

            Link<Void> avbryt = new BookmarkablePageLink<>("avbryt", Innboks.class);

            add(fritekst, feedbackPanel, send, avbryt);
        }

        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(SendPanel.class, "textarea.js")));
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
