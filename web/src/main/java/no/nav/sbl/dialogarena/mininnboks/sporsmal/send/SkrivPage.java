package no.nav.sbl.dialogarena.mininnboks.sporsmal.send;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextArea;
import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.innboks.Innboks;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.Sporsmal;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.kvittering.KvitteringPage;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.TemagruppeDropdown;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
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

public class SkrivPage extends BasePage {

    private static final Logger LOG = LoggerFactory.getLogger(SkrivPage.class);
    public static final String IKKE_AKSEPTERT_FEILMELDING_PROPERTY = "send-sporsmal.still-sporsmal.betingelser.feilmelding.ikke-akseptert";
    public static final String UNDERLIGGENDE_FEIL_FEILMELDING_PROPERTY = "send-sporsmal.still-sporsmal.underliggende-feil";

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

        private SporsmalForm(String id, final CompoundPropertyModel<Sporsmal> model) {
            super(id, model);

            EnhancedTextArea enhancedTextArea = new EnhancedTextArea("tekstfelt", model);

            final FeedbackPanel feedbackPanel = new FeedbackPanel("validering");
            feedbackPanel.setOutputMarkupId(true);

            AjaxSubmitLink send = new AjaxSubmitLink("send") {
                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    Sporsmal spsm = getModelObject();
                    if (spsm.betingelserAkseptert) {
                        try {
                            spsm.innsendingsTidspunkt = DateTime.now();
                            service.stillSporsmal(spsm.getFritekst(), spsm.getTemagruppe(), SubjectHandler.getSubjectHandler().getUid());
                            setResponsePage(KvitteringPage.class);
                        } catch (Exception e) {
                            LOG.error("Feil ved innsending av spørsmål", e);
                            error(getString(UNDERLIGGENDE_FEIL_FEILMELDING_PROPERTY));
                            target.add(feedbackPanel);
                        }
                    } else {
                        error(getString(IKKE_AKSEPTERT_FEILMELDING_PROPERTY));
                        target.add(feedbackPanel);
                    }
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.add(feedbackPanel);
                }
            };

            Link<Void> avbryt = new BookmarkablePageLink<>("avbryt", Innboks.class);

            IModel<Temagruppe> temagruppeSelected = new PropertyModel<>(getModelObject(), "temagruppe");
            TemagruppeDropdown temagruppeDropdown = new TemagruppeDropdown("temagruppeDropdown", temagruppeSelected);

            add(new BetingelseValgPanel("betingelseValg", model));

            add(temagruppeDropdown, enhancedTextArea, feedbackPanel, send, avbryt);
        }

        @Override
        public void renderHead(IHeaderResponse response) {
            super.renderHead(response);
            response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(SkrivPage.class, "skriv.js")));
        }
    }

}
