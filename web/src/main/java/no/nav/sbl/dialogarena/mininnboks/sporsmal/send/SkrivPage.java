package no.nav.sbl.dialogarena.mininnboks.sporsmal.send;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.modig.wicket.errorhandling.aria.AriaFeedbackPanel;
import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import no.nav.sbl.dialogarena.mininnboks.innboks.Innboks;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.Sporsmal;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.kvittering.KvitteringPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.*;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.FeedbackUtils.componentHasErrors;
import static no.nav.sbl.dialogarena.mininnboks.sporsmal.FeedbackUtils.numberOfErrorMessages;
import static org.apache.wicket.AttributeModifier.append;

public class SkrivPage extends BasePage {

    private static final Logger LOG = LoggerFactory.getLogger(SkrivPage.class);
    public static final String IKKE_AKSEPTERT_FEILMELDING_PROPERTY = "send-sporsmal.still-sporsmal.betingelser.feilmelding.ikke-akseptert";
    public static final String UNDERLIGGENDE_FEIL_FEILMELDING_PROPERTY = "send-sporsmal.still-sporsmal.underliggende-feil";
    public static final JavaScriptResourceReference JQUERY_JS = new JavaScriptResourceReference(SkrivPage.class, "jquery-ui-selectmenu.min.js");
    public static final CssResourceReference JQUERY_CSS = new CssResourceReference(SkrivPage.class, "jquery-ui.css");

    @Inject
    private HenvendelseService service;

    public SkrivPage(PageParameters parameters) {

        Sporsmal sporsmal = new Sporsmal();
        sporsmal.setTemagruppe(extractTemagruppe(parameters.get("temagruppe").toString()));
        add(new SporsmalForm("sporsmalForm", new CompoundPropertyModel<>(sporsmal)));

        IModel<Boolean> ikkeTilgang = not(new PropertyModel<Boolean>(get("sporsmalForm"), "isRenderAllowed"));
        WebMarkupContainer tilInnboksWrapper = new WebMarkupContainer("tilInnboksWrapper");
        tilInnboksWrapper.add(new BookmarkablePageLink<>("tilInnboks", Innboks.class));
        add(new WebMarkupContainer("diskresjonskode").add(visibleIf(ikkeTilgang)));
        add(tilInnboksWrapper.add(visibleIf(ikkeTilgang)));
    }

    private Temagruppe extractTemagruppe(String temagruppeParameter) {
        Temagruppe temagruppe = Temagruppe.valueOf(temagruppeParameter);
        if(!Temagruppe.GODKJENTE_FOR_INNGAAENDE_SPORSMAAL.contains(temagruppe)){
            throw new RuntimeException("Temagruppe " + temagruppeParameter + " er ikke gydlig.");
        }
        return temagruppe;
    }

    private final class SporsmalForm extends Form<Sporsmal> {
        public static final String PLACEHOLDER_TEXT_KEY = "skriv-sporsmal.fritekst.placeholder";
        private final EnhancedTextAreaConfigurator textAreaConfigurator;

        private SporsmalForm(String id, final CompoundPropertyModel<Sporsmal> model) {
            super(id, model);

            String valgtTemagruppe = getString(model.getObject().getTemagruppe().name());
            Label temagruppe = new Label("temagruppe", valgtTemagruppe);
            temagruppe.add(AttributeModifier.replace("aria-label", getString("send-sporsmal.tema.tittel") + valgtTemagruppe));

            final AriaFeedbackPanel feedbackPanel = new AriaFeedbackPanel("validering");
            feedbackPanel.setOutputMarkupPlaceholderTag(true);

            textAreaConfigurator = new EnhancedTextAreaConfigurator().withPlaceholderTextKey(PLACEHOLDER_TEXT_KEY);
            final EnhancedTextArea enhancedTextArea = new EnhancedTextArea("tekstfelt", model, textAreaConfigurator);
            enhancedTextArea.get("text").add(append("aria-label", new ResourceModel(PLACEHOLDER_TEXT_KEY)));

            final Label tekstfeltFeilmelding = new Label("tekstfelt-feilmelding", "Tekstfeltet kan ikke være tomt.");
            tekstfeltFeilmelding.setOutputMarkupPlaceholderTag(true);
            tekstfeltFeilmelding.add(
                    visibleIf(both(
                                    componentHasErrors(enhancedTextArea.get("text"), feedbackPanel))
                                    .and(numberOfErrorMessages(feedbackPanel, 1))
                    )
            );


            final BetingelseValgPanel betingelseValgPanel = new BetingelseValgPanel("betingelseValg", model, feedbackPanel);
            IndicatingAjaxButton send = new IndicatingAjaxButton("send", this) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form) {
                    Sporsmal spsm = model.getObject();
                    try {
                        spsm.innsendingsTidspunkt = DateTime.now();
                        Henvendelse henvendelse = new Henvendelse(spsm.getFritekst(), spsm.getTemagruppe());
                        service.stillSporsmal(henvendelse, getSubjectHandler().getUid());
                        setResponsePage(KvitteringPage.class);
                    } catch (Exception e) {
                        LOG.error("Feil ved innsending av spørsmål", e);
                        error(getString(UNDERLIGGENDE_FEIL_FEILMELDING_PROPERTY));
                        betingelseValgPanel.oppdater(target);
                        target.add(feedbackPanel, tekstfeltFeilmelding);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedbackPanel, tekstfeltFeilmelding);
                    betingelseValgPanel.oppdater(target);
                    target.appendJavaScript("Validator.validateAll()");
                }
            };

            Link<Void> avbryt = new BookmarkablePageLink<>("avbryt", Innboks.class);

            feedbackPanel.add(visibleIf(either(
                                    numberOfErrorMessages(feedbackPanel, 2))
                                    .or(componentHasErrors(send, feedbackPanel))
                    )
            );

            add(temagruppe, enhancedTextArea, tekstfeltFeilmelding, feedbackPanel, send, avbryt);
            add(betingelseValgPanel);
        }


        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            String jsValidatorConfig = String.format("{'form': '%s','textareaPlaceholder': '%s', 'textareaErrorMessage': '%s', 'textareaErrorMessageLength': '%s', 'checkboxErrorMessage': '%s', maxLength: %d}",
                    ".mininnboks-omslag form",
                    getString(PLACEHOLDER_TEXT_KEY),
                    getString("send-sporsmal.still-sporsmal.text.tomt"),
                    getString("send-sporsmal.still-sporsmal.text.forlang"),
                    getString(IKKE_AKSEPTERT_FEILMELDING_PROPERTY),
                    textAreaConfigurator.getMaxCharCount());

            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(SkrivPage.class, "skriv.js")));
            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(BasePage.class, "skrivValidator.js")));
            response.render(OnDomReadyHeaderItem.forScript("window.Validator = new SkrivFormValidator(" + jsValidatorConfig + ");"));

        }
    }

}
