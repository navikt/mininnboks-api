package no.nav.sbl.dialogarena.mininnboks.sporsmal.send;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.innboks.Innboks;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.Sporsmal;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.kvittering.KvitteringPage;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractRangeValidator;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class SkrivPage extends BasePage {

    @Inject
    private HenvendelseService service;
    private static final Logger LOG = LoggerFactory.getLogger(SkrivPage.class);

    public SkrivPage(PageParameters parameters) {
        Sporsmal sporsmal = new Sporsmal();
        sporsmal.setTema(Tema.valueOf(parameters.get("tema").toString()));
        CompoundPropertyModel<Sporsmal> model = new CompoundPropertyModel<>(sporsmal);
        add(new SporsmalForm("sporsmal-form", model));
    }

    private final class SporsmalForm extends Form<Sporsmal> {

        private static final int FRITEKST_MAKS_LENGDE = 1000;

        private SporsmalForm(String id, final CompoundPropertyModel<Sporsmal> model) {
            super(id, model);

            final Label temaOverskrift = new Label("tema", new StringResourceModel("${tema}", model));
            temaOverskrift.setOutputMarkupId(true);

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
                        service.stillSporsmal(spsm.getFritekst(), spsm.getTema(), SubjectHandler.getSubjectHandler().getUid());
                        setResponsePage(KvitteringPage.class);
                    } catch (Exception e) {
                        LOG.error("Feil ved innsending av spørsmål", e);
                        error(new StringResourceModel("send-sporsmal.still-sporsmal.underliggende-feil", SkrivPage.this, null).getString());
                        target.add(feedbackPanel);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedbackPanel);
                }
            };

            Link<Void> avbryt = new BookmarkablePageLink<>("avbryt", Innboks.class);

            final WebMarkupContainer endreTemaWrapper = new WebMarkupContainer("endre-tema-wrapper");
            endreTemaWrapper.setOutputMarkupId(true);
            ListView<Tema> endreTema = new ListView<Tema>("tema-liste", asList(Tema.values())) {
                @Override
                protected void populateItem(final ListItem<Tema> item) {
                    final Label tema = new Label("tema", new ResourceModel(item.getModelObject().toString()));
                    tema.add(hasCssClassIf("valgt", Model.of(item.getModelObject() == SporsmalForm.this.getModelObject().getTema())));
                    tema.add(new AjaxEventBehavior("click") {
                        @Override
                        protected void onEvent(AjaxRequestTarget target) {
                            SporsmalForm.this.getModelObject().setTema(item.getModelObject());
                            target.add(temaOverskrift, endreTemaWrapper);
                        }
                    });
                    item.add(tema);
                }
            };
            endreTemaWrapper.add(endreTema);

            add(temaOverskrift, endreTemaWrapper, fritekst, feedbackPanel, send, avbryt);
        }

        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(SkrivPage.class, "skriv.js")));
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