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
    HenvendelseService service;

    private InnboksModell innboksModell;
    AjaxLink<Void> tilInnboksLink;

    public Innboks() {
        innboksModell = new InnboksModell(new InnboksVM(service.hentAlleHenvendelser(innloggetBruker())));
        setDefaultModel(innboksModell);
        setOutputMarkupId(true);

        WebMarkupContainer tomInnboks = new WebMarkupContainer("tom-innboks");
        tomInnboks.add(new Label("tom-innboks-tekst", new StringResourceModel("innboks.tom-innboks-melding", this, null)));
        tomInnboks.add(hasCssClassIf("ingen-meldinger", new Model(!innboksModell.ingenHenvendelser().getObject())));

        final AlleHenvendelserPanel alleMeldinger = new AlleHenvendelserPanel("henvendelser", innboksModell, service);
        alleMeldinger.add(hasCssClassIf("skjult", innboksModell.alleHenvendelserSkalSkjulesHvisLitenSkjerm));
        alleMeldinger.add(hasCssClassIf("ingen-meldinger", innboksModell.ingenHenvendelser()));

        DetaljvisningPanel detaljvisning = new DetaljvisningPanel("detaljpanel", innboksModell);
        detaljvisning.add(hasCssClassIf("ingen-meldinger", innboksModell.ingenHenvendelser()));

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
                innboksModell.alleHenvendelserSkalSkjulesHvisLitenSkjerm.setObject(false);
                target.add(this, alleMeldinger);
            }
        };
        tilInnboksLink.add(hasCssClassIf("skjult", not(innboksModell.alleHenvendelserSkalSkjulesHvisLitenSkjerm)));
        topBar.add(tilInnboksLink);

        add(topBar, tomInnboks, alleMeldinger, detaljvisning);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Innboks.class, "innboks.js")));
    }

    @RunOnEvents(OPPDATER_HENVENDELSER)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        this.innboksModell.getObject().oppdaterHenvendelserFra(service.hentAlleHenvendelser(innloggetBruker()));
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
