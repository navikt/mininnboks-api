package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import javax.inject.Inject;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.MeldingService;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal.SendSporsmalPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class Innboks extends BasePage {

    public static final String VALGT_MELDING = "hendelser.valgt_melding";
    public static final String OPPDATER_MELDINGER = "hendelser.oppdater_meldinger";

    @Inject
    MeldingService service;

    private InnboksModell innboksModell;
    String fodselsnr;

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

        AlleMeldingerPanel alleMeldinger = new AlleMeldingerPanel("meldinger", innboksModell, service);
        DetaljvisningPanel detaljvisning = new DetaljvisningPanel("detaljpanel", innboksModell);
        add(topBar, alleMeldinger, detaljvisning);

        add(new AttributeAppender("class", " innboks clearfix"));
    }

    @RunOnEvents(OPPDATER_MELDINGER)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        this.innboksModell.getObject().oppdaterMeldingerFra(service.hentAlleMeldinger(fodselsnr));
        target.add(this);
    }

}
