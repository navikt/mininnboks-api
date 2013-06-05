package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdService;
import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.components.BehandlingerUnderArbeidListView;
import no.nav.sbl.dialogarena.minehenvendelser.components.FerdigeBehandlingerListView;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.webkomponent.tilbakemelding.service.TilbakemeldingService;
import no.nav.sbl.dialogarena.webkomponent.tilbakemelding.web.TilbakemeldingContainer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.List;

import static java.lang.String.valueOf;
import static java.lang.System.getProperty;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmpty;
import static no.nav.sbl.dialogarena.minehenvendelser.ApplicationConstants.APPLICATION_NAME;
import static org.apache.wicket.model.Model.of;

/**
 * Hovedside for applikasjonen. Laster inn behandlinger via. en service og
 * instansierer wicketmodellene med lister av innsendte og uferdige søknader
 */
public class HomePage extends BasePage {

    @Inject
    private BehandlingService behandlingService;

    @Inject
    private AktoerIdService aktoerIdService;

    @Inject
    private TilbakemeldingService tilbakemeldingService;

    @Inject
    private Boolean tilbakemeldingEnabled;

    public HomePage(PageParameters pageParameters) {
        checkPageParametersAndSetAktoerId(pageParameters);
        List<Behandling> behandlinger = behandlingService.hentBehandlinger(aktoerIdService.getAktoerId());

        add(
                new Label("hovedTittel", innholdstekster.hentTekst("hoved.tittel")),
                new WebMarkupContainer("tooltip").add(new AttributeAppender("title", of(innholdstekster.hentTekst("tooltip.tekst")))),
                new FerdigeBehandlingerListView("behandlingerFerdig", behandlinger),
                new BehandlingerUnderArbeidListView("behandlingerUnderArbeid", behandlinger),
                new IngenBehandlingerView("ingenBehandlinger", behandlinger),
                new ExternalLink("forsiden", getProperty("inngangsporten.link.url"), innholdstekster.hentTekst("link.tekst.forsiden")),
                new TilbakemeldingContainer("panel-tilbakemelding", APPLICATION_NAME, tilbakemeldingService, tilbakemeldingEnabled)
        );
    }

    private void checkPageParametersAndSetAktoerId(PageParameters pageParameters) {
        if (pageParametersContainAktoerId(pageParameters)) {
            aktoerIdService.setAktoerId(valueOf(pageParameters.get("aktoerId")));
        } else {
            aktoerIdService.setAktoerId(null);
        }
    }

    private boolean pageParametersContainAktoerId(PageParameters pageParameters) {
        return pageParameters.get("aktoerId") != null && !pageParameters.get("aktoerId").isEmpty();
    }

    private class IngenBehandlingerView extends WebMarkupContainer {

        public IngenBehandlingerView(String id, List<Behandling> behandlinger) {
            super(id);

            add(visibleIf(isEmpty(behandlinger)));
            add(
                    new Label("ingenInnsendingerTittel", innholdstekster.hentTekst("ingen.innsendinger.tittel")),
                    new Label("ingenInnsendingerTekst", innholdstekster.hentTekst("ingen.innsendinger.tekst")),
                    new ExternalLink("skjemaerLink", "#", innholdstekster.hentTekst("ingen.innsendinger.link.tekst.skjemaer")));
        }

    }

}
