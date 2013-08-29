package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.MeldingService;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal.SendSporsmalPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.model.ModelUtils.not;

public class Innboks extends BasePage {

    public static final String VALGT_MELDING = "hendelser.valgt_melding";
    public static final String OPPDATER_MELDINGER = "hendelser.oppdater_meldinger";

    @Inject
    MeldingService service;

    private InnboksModell innboksModell;
    String fodselsnr;
    AjaxLink<Void> tilInnboksLink;

    public Innboks(PageParameters pageParameters) {
        fodselsnr = pageParameters.get("fnr").toString();
        innboksModell = new InnboksModell(new InnboksVM(service.hentAlleMeldinger(fodselsnr)));
        setDefaultModel(innboksModell);
        setOutputMarkupId(true);

        WebMarkupContainer topBar = new WebMarkupContainer("top-bar");
        topBar.add(new Link("skriv-ny") {
            @Override
            public void onClick() {
                setResponsePage(SendSporsmalPage.class);
            }
        });

        final AlleMeldingerPanel alleMeldinger = new AlleMeldingerPanel("meldinger", innboksModell, service);
        alleMeldinger.add(hasCssClassIf("skjult", innboksModell.alleMeldingerSkalSkjules()));
        DetaljvisningPanel detaljvisning = new DetaljvisningPanel("detaljpanel", innboksModell);

        tilInnboksLink = new AjaxLink<Void>("til-innboks") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                innboksModell.skjulAlleMeldingerHvisResponsiv = false;
                target.add(this, alleMeldinger);
            }
        };
        tilInnboksLink.add(hasCssClassIf("skjult", not(innboksModell.alleMeldingerSkalSkjules())));
        topBar.add(tilInnboksLink);

        add(topBar, alleMeldinger, detaljvisning);
    }

    @RunOnEvents(OPPDATER_MELDINGER)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        this.innboksModell.getObject().oppdaterMeldingerFra(service.hentAlleMeldinger(fodselsnr));
        target.add(this);
    }

    @RunOnEvents(VALGT_MELDING)
    public void visTilInnboksLink(AjaxRequestTarget target) {
        target.add(tilInnboksLink);
    }
}
