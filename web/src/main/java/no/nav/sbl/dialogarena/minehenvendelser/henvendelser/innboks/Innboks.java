package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.innboks;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer.MeldingService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.panel.Panel;

public class Innboks extends Panel {

    public static final String VALGT_MELDING = "hendelser.valgt_melding";
    public static final String OPPDATER_MELDINGER = "hendelser.oppdater_meldinger";

    private InnboksModell innboksModell;
    private MeldingService service;
    private String fodselsnr;

    public Innboks(String id, String fodselsnr, MeldingService service) {
        super(id);
        this.fodselsnr = fodselsnr;
        this.service = service;
        innboksModell = new InnboksModell(new InnboksVM(service.hentAlleMeldinger(fodselsnr)));
        setDefaultModel(innboksModell);
        setOutputMarkupId(true);

        AlleMeldingerPanel alleMeldinger = new AlleMeldingerPanel("meldinger", innboksModell, service);
        DetaljvisningPanel detaljvisning = new DetaljvisningPanel("detaljpanel", innboksModell);
        add(alleMeldinger, detaljvisning);

        add(new AttributeAppender("class", " innboks clearfix"));
    }

    @SuppressWarnings("unused")
    @RunOnEvents(OPPDATER_MELDINGER)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        this.innboksModell.getObject().oppdaterMeldingerFra(service.hentAlleMeldinger(fodselsnr));
        target.add(this);
    }

}
