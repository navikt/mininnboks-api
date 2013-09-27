package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

import javax.inject.Inject;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.HenvendelseService;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal.SendSporsmalPage;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class Innboks extends BasePage {

    public static final String VALGT_HENVENDELSE = "hendelser.valgt_henvendelse";
    public static final String OPPDATER_HENVENDELSER = "hendelser.oppdater_henvendelser";

    @Inject
    HenvendelseService service;

    private InnboksModell innboksModell;
    AjaxLink<Void> tilInnboksLink;

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(new JavaScriptResourceReference(Innboks.class, "innboks.js")));
    }

    public Innboks() {
        innboksModell = new InnboksModell(new InnboksVM(service.hentAlleHenvendelser(innloggetBruker())));
        setDefaultModel(innboksModell);
        setOutputMarkupId(true);

        WebMarkupContainer topBar = new WebMarkupContainer("top-bar");
        topBar.add(new Link("skriv-ny") {
            @Override
            public void onClick() {
                setResponsePage(SendSporsmalPage.class);
            }
        });

        final AlleHenvendelserPanel alleMeldinger = new AlleHenvendelserPanel("henvendelser", innboksModell, service);
        alleMeldinger.add(hasCssClassIf("skjult", innboksModell.alleHenvendelserSkalSkjulesHvisLitenSkjerm));
        DetaljvisningPanel detaljvisning = new DetaljvisningPanel("detaljpanel", innboksModell);

        tilInnboksLink = new AjaxLink<Void>("til-innboks") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                innboksModell.alleHenvendelserSkalSkjulesHvisLitenSkjerm.setObject(false);
                target.add(this, alleMeldinger);
            }
        };
        tilInnboksLink.add(hasCssClassIf("skjult", not(innboksModell.alleHenvendelserSkalSkjulesHvisLitenSkjerm)));
        topBar.add(tilInnboksLink);

        add(topBar, alleMeldinger, detaljvisning);
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
