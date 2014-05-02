package no.nav.sbl.dialogarena.minehenvendelser.innboks;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.tema.VelgTemaPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class Innboks extends BasePage {

    public static final String VALGT_HENVENDELSE = "hendelser.valgt_henvendelse";
    public static final String OPPDATER_HENVENDELSER = "hendelser.oppdater_henvendelser";

    @Inject
    private HenvendelseService service;

    private InnboksVM innboksVM;
    private AjaxLink<Void> tilInnboksLink;

    public Innboks() {
        innboksVM = new InnboksVM(service.hentAlleHenvendelser(innloggetBruker()));
        setDefaultModel(new CompoundPropertyModel<>(innboksVM));
        setOutputMarkupId(true);

        WebMarkupContainer tomInnboks = new WebMarkupContainer("tom-innboks");
        tomInnboks.add(new Label("tom-innboks-tekst", new StringResourceModel("innboks.tom-innboks-melding", this, null)));
        tomInnboks.add(hasCssClassIf("ingen-meldinger", new Model<>(!innboksVM.ingenHenvendelser().getObject())));

        final AlleHenvendelserPanel alleMeldinger = new AlleHenvendelserPanel("henvendelser", innboksVM, service);
        alleMeldinger.add(hasCssClassIf("skjult", innboksVM.alleHenvendelserSkalSkjulesHvisLitenSkjerm));
        alleMeldinger.add(hasCssClassIf("ingen-meldinger", innboksVM.ingenHenvendelser()));

        DetaljvisningPanel detaljvisning = new DetaljvisningPanel("detaljpanel", innboksVM);
        detaljvisning.add(hasCssClassIf("ingen-meldinger", innboksVM.ingenHenvendelser()));

        WebMarkupContainer topBar = new WebMarkupContainer("top-bar");
        topBar.add(new Link<Void>("skriv-ny") {
            @Override
            public void onClick() {
                setResponsePage(VelgTemaPage.class);
            }
        });
        tilInnboksLink = new AjaxLink<Void>("til-innboks") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                innboksVM.alleHenvendelserSkalSkjulesHvisLitenSkjerm.setObject(false);
                target.add(this, alleMeldinger);
            }
        };
        tilInnboksLink.add(hasCssClassIf("skjult", not(innboksVM.alleHenvendelserSkalSkjulesHvisLitenSkjerm)));
        topBar.add(tilInnboksLink);

        add(topBar, tomInnboks, alleMeldinger, detaljvisning);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Innboks.class, "innboks.js")));
    }

    @RunOnEvents(OPPDATER_HENVENDELSER)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        innboksVM.oppdaterHenvendelserFra(service.hentAlleHenvendelser(innloggetBruker()));
        target.add(this);
    }

    @RunOnEvents(VALGT_HENVENDELSE)
    public void visTilInnboksLink(AjaxRequestTarget target) {
        target.add(tilInnboksLink);
    }

    private static String innloggetBruker() {
        return SubjectHandler.getSubjectHandler().getUid();
    }
}
