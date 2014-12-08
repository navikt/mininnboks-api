package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.*;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.WicketAutorizationUtils.accessRestriction;
import static no.nav.modig.wicket.conditional.ConditionalUtils.*;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.mininnboks.innboks.TraadVM.erLest;
import static no.nav.sbl.dialogarena.mininnboks.innboks.TraadVM.tilTraader;
import static org.apache.wicket.AttributeModifier.append;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

public class Innboks extends BasePage {

    public static final String TOM_INNBOKS = "innboks.tom-innboks-melding";
    public static final String EXCEPTION = "innboks.kunne-ikke-hente-meldinger";

    private final IModel<List<TraadVM>> traaderModel;
    @Inject
    private HenvendelseService service;

    private IModel<Boolean> kunneIkkeHenteTraader = Model.of(false);

    public Innboks() {
        traaderModel = new CompoundPropertyModel<>(hentTraader());

        final ExternalLink skrivNyKnapp = new ExternalLink("skrivNy", System.getProperty("temavelger.link.url"));

        add(skrivNyKnapp.add(accessRestriction(RENDER).withAttributes(actionId("innsending"), resourceId(""))));
        add(new WebMarkupContainer("diskresjonskode").add(visibleIf(not(new PropertyModel<Boolean>(skrivNyKnapp, "isRenderAllowed")))));

        add(new ListView<TraadVM>("traader", traaderModel) {
            @Override
            protected void populateItem(final ListItem<TraadVM> item) {
                final TraadVM traadVM = item.getModelObject();
                item.setOutputMarkupId(true);

                item.add(hasCssClassIf("lest", erLest(traadVM.henvendelser)));
                item.add(hasCssClassIf("closed", traadVM.lukket));

                AjaxLink<Void> flipp = new AjaxLink<Void>("flipp") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        traadClickBehaviour(item, target);
                    }
                };
                flipp.add(attributeIf("aria-pressed", "true", not(traadVM.lukket), true));

                Label ariahelper = new Label("ariahelper", traadVM.ariaTekst);

                WebMarkupContainer traadcontainer = new WebMarkupContainer("traadcontainer");
                traadcontainer.add(attributeIf("aria-expanded", "true", not(traadVM.lukket), true));

                TidligereMeldingerPanel tidligereMeldinger = new TidligereMeldingerPanel("tidligereMeldinger", item.getModel());

                NyesteMeldingPanel nyesteMelding = new NyesteMeldingPanel("nyesteMelding", item.getModel());

                flipp.add(append("aria-controls", traadcontainer.getMarkupId()));
                flipp.add(append("aria-labelledby", ariahelper.getMarkupId()));

                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        if (traadVM.lukket.getObject()) {
                            traadClickBehaviour(item, target);
                        }
                    }

                    @Override
                    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                        super.updateAjaxAttributes(attributes);
                        attributes.setAllowDefault(true);
                    }
                });
                flipp.add(ariahelper);
                traadcontainer.add(nyesteMelding, tidligereMeldinger);
                item.add(flipp, traadcontainer);
            }
        });
        WebMarkupContainer innboksTilbakeMeldingWrapper = new WebMarkupContainer("tomInnboks");
        innboksTilbakeMeldingWrapper.add(new Label("innboks-tilbakemelding", getTilbakeMeldingModel()).setEscapeModelStrings(false));
        add(innboksTilbakeMeldingWrapper.add(hasCssClassIf("ingen-meldinger", tomInnboksModel())));
    }

    private IModel<String> getTilbakeMeldingModel() {
        return new ResourceModel(kunneIkkeHenteTraader.getObject() ? EXCEPTION : TOM_INNBOKS);
    }

    private List<TraadVM> hentTraader() {
        try {
            return tilTraader(service.hentAlleHenvendelser(innloggetBruker()));
        } catch (Exception e) {
            kunneIkkeHenteTraader.setObject(true);
            return asList();
        }
    }

    private static String innloggetBruker() {
        return SubjectHandler.getSubjectHandler().getUid();
    }

    private void traadClickBehaviour(ListItem<TraadVM> item, AjaxRequestTarget target) {
        IModel<TraadVM> traadVM = item.getModel();

        if (!erLest(traadVM.getObject().henvendelser).getObject()) {
            traadVM.getObject().markerSomLest(service);
        }
        traadVM.getObject().lukket.setObject(!traadVM.getObject().lukket.getObject());
        target.add(item);
    }

    private IModel<Boolean> tomInnboksModel() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return !traaderModel.getObject().isEmpty();
            }
        };
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(forReference(new JavaScriptResourceReference(Innboks.class, "innboks.js")));
    }

    @Override
    protected void onBeforeRender() {
        traaderModel.setObject(hentTraader());
        super.onBeforeRender();
    }
}
