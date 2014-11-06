package no.nav.sbl.dialogarena.mininnboks.sporsmal.send;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaConfigurator;
import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.innboks.Innboks;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.Sporsmal;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.kvittering.KvitteringPage;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe;
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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
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

import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.WicketAutorizationUtils.accessRestriction;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
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
        sporsmal.setTemagruppe(Temagruppe.valueOf(parameters.get("temagruppe").toString()));
        add(new SporsmalForm("sporsmalForm", new CompoundPropertyModel<>(sporsmal))
                .add(accessRestriction(RENDER).withAttributes(actionId("innsending"), resourceId(""))));

        IModel<Boolean> ikkeTilgang = not(new PropertyModel<Boolean>(get("sporsmalForm"), "isRenderAllowed"));
        add(new WebMarkupContainer("diskresjonskode").add(visibleIf(ikkeTilgang)));
        add(new BookmarkablePageLink<>("tilInnboks", Innboks.class).add(visibleIf(ikkeTilgang)));
    }

    private final class SporsmalForm extends Form<Sporsmal> {
        public static final String PLACEHOLDER_TEXT_KEY = "skriv-sporsmal.fritekst.placeholder";

        private SporsmalForm(String id, final CompoundPropertyModel<Sporsmal> model) {
            super(id, model);

            Label temagruppe = new Label("temagruppe", getString(model.getObject().getTemagruppe().name()));

            EnhancedTextAreaConfigurator config = new EnhancedTextAreaConfigurator().withPlaceholderTextKey(PLACEHOLDER_TEXT_KEY);
            EnhancedTextArea enhancedTextArea = new EnhancedTextArea("tekstfelt", model, config);
            enhancedTextArea.get("text").add(append("aria-label", new ResourceModel(PLACEHOLDER_TEXT_KEY)));

            final FeedbackPanel feedbackPanel = new AriaFeedbackPanel("validering");
            feedbackPanel.setOutputMarkupId(true);

            IndicatingAjaxButton send = new IndicatingAjaxButton("send", this) {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form form) {
                    Sporsmal spsm = model.getObject();
                    try {
                        spsm.innsendingsTidspunkt = DateTime.now();
                        service.stillSporsmal(spsm.getFritekst(), spsm.getTemagruppe(), SubjectHandler.getSubjectHandler().getUid());
                        setResponsePage(KvitteringPage.class);
                    } catch (Exception e) {
                        LOG.error("Feil ved innsending av spørsmål", e);
                        error(getString(UNDERLIGGENDE_FEIL_FEILMELDING_PROPERTY));
                        target.add(feedbackPanel);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.appendJavaScript("SkrivFormValidator.validateAll()");
                    target.add(feedbackPanel);
                }
            };

            Link<Void> avbryt = new BookmarkablePageLink<>("avbryt", Innboks.class);

            add(temagruppe, enhancedTextArea, feedbackPanel, send, avbryt);
            add(new BetingelseValgPanel("betingelseValg", model));
        }

        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            String jsValidatorConfig = String.format("{'form': %s,'textareaPlaceholder': '%s', 'textareaErrorMessage': '%s', 'checkboxErrorMessage': '%s'}",
                    "$('.mininnboks-omslag form').get(0)",
                    new ResourceModel(PLACEHOLDER_TEXT_KEY).getObject(),
                    new ResourceModel("send-sporsmal.still-sporsmal.text.tomt").getObject(),
                    new ResourceModel(IKKE_AKSEPTERT_FEILMELDING_PROPERTY).getObject());

            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(SkrivPage.class, "skriv.js")));
            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(SkrivPage.class, "skrivValidator.js")));
            response.render(OnDomReadyHeaderItem.forScript("window.SkrivFormValidator = new SkrivFormValidator(" + jsValidatorConfig + ");"));

        }
    }

}
