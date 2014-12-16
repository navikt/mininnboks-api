package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.wicket.events.annotations.RefreshOnEvents;
import no.nav.sbl.dialogarena.mininnboks.BasePage;
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.mininnboks.innboks.traader.TraadVM;
import no.nav.sbl.dialogarena.mininnboks.innboks.traader.TraaderListView;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.util.string.StringValue;

import javax.inject.Inject;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.WicketAutorizationUtils.accessRestriction;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.not;
import static no.nav.sbl.dialogarena.mininnboks.innboks.traader.TraadVM.tilTraader;
import static no.nav.sbl.dialogarena.mininnboks.utils.Event.HENVENDELSE_BESVART;
import static org.apache.wicket.markup.head.JavaScriptHeaderItem.forReference;

@RefreshOnEvents(HENVENDELSE_BESVART)
public class Innboks extends BasePage<List<TraadVM>> {

    public static final String TOM_INNBOKS = "innboks.tom-innboks-melding";
    public static final String EXCEPTION = "innboks.kunne-ikke-hente-meldinger";
    public static final String TRAAD_ID_PARAMETER_NAME = "id";

    @Inject
    private HenvendelseService service;

    private IModel<Boolean> kunneIkkeHenteTraader = Model.of(false);

    public Innboks(PageParameters parameters) {
        setModel(new CompoundPropertyModel<>(hentTraader()));

        final StringValue valgtTraadId = parameters.get(TRAAD_ID_PARAMETER_NAME);
        final ExternalLink skrivNyKnapp = new ExternalLink("skrivNy", System.getProperty("temavelger.link.url"));

        WebMarkupContainer innboksTilbakeMeldingWrapper = new WebMarkupContainer("tomInnboks");
        innboksTilbakeMeldingWrapper.add(new Label("innboks-tilbakemelding", getTilbakeMeldingModel()).setEscapeModelStrings(false));

        add(skrivNyKnapp.add(accessRestriction(RENDER).withAttributes(actionId("innsending"), resourceId(""))));
        add(new WebMarkupContainer("diskresjonskode").add(visibleIf(not(new PropertyModel<Boolean>(skrivNyKnapp, "isRenderAllowed")))));
        add(new TraaderListView("traader", getModel(), valgtTraadId));
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

    private IModel<Boolean> tomInnboksModel() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return !getModelObject().isEmpty();
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
        setModelObject(hentTraader());
        super.onBeforeRender();
    }
}
