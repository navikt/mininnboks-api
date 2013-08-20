package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.FoedselsnummerService;
import no.nav.sbl.dialogarena.minehenvendelser.components.behandling.BehandlingerUnderArbeidListView;
import no.nav.sbl.dialogarena.minehenvendelser.components.soeknader.FerdigeSoeknaderListView;
import no.nav.sbl.dialogarena.minehenvendelser.components.soeknader.MottatteSoeknaderListView;
import no.nav.sbl.dialogarena.minehenvendelser.components.soeknader.SoeknaderUnderArbeidListView;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.SakogbehandlingService;
import no.nav.sbl.dialogarena.webkomponent.tilbakemelding.service.TilbakemeldingService;
import no.nav.sbl.dialogarena.webkomponent.tilbakemelding.web.TilbakemeldingContainer;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;

import javax.inject.Inject;
import java.util.List;

import static java.lang.System.getProperty;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.isEmpty;

/**
 * Hovedside for applikasjonen. Laster inn behandlinger via. en service og
 * instansierer wicketmodellene med lister av innsendte og uferdige søknader
 */
public class HomePage extends BasePage {

    @Inject
    private BehandlingService behandlingService;

    @Inject
    private FoedselsnummerService foedselsnummerService;

    @Inject
    private TilbakemeldingService tilbakemeldingService;

    @Inject
    private SakogbehandlingService sakogbehandlingService;

    @Inject
    private Boolean tilbakemeldingEnabled;

    public HomePage() {
        List<Henvendelsesbehandling> behandlinger = behandlingService.hentPabegynteBehandlinger(foedselsnummerService.getFoedselsnummer());

        add(
                new Label("hovedTittel", cmsContentRetriever.hentTekst("hoved.tittel")),
                createTooltip(),
                new Label("paabegynteTittel", cmsContentRetriever.hentTekst("soeknad.paabegynte.tittel")),
                new Label("underarbeidTittel", cmsContentRetriever.hentTekst("soeknad.underarbeid.tittel")),
                new Label("ferdigTittel", cmsContentRetriever.hentTekst("soeknad.ferdige.tittel")),
                new Label("mottattTittel", cmsContentRetriever.hentTekst("soeknad.mottatt.tittel")),
                new BehandlingerUnderArbeidListView("behandlingerUnderArbeid", behandlinger),
                new MottatteSoeknaderListView("mottatteSoeknader", sakogbehandlingService.finnMottatteSoeknader(foedselsnummerService.getFoedselsnummer())),
                new SoeknaderUnderArbeidListView("soeknaderUnderArbeid", sakogbehandlingService.finnSoeknaderUnderArbeid(foedselsnummerService.getFoedselsnummer())),
                new FerdigeSoeknaderListView("ferdigeSoeknader", sakogbehandlingService.finnFerdigeSoeknader(foedselsnummerService.getFoedselsnummer())),
                new IngenBehandlingerView("ingenBehandlinger", behandlinger),
                new ExternalLink("forsiden", getProperty("inngangsporten.link.url"), cmsContentRetriever.hentTekst("link.tekst.forsiden")),
                new TilbakemeldingContainer("panel-tilbakemelding", tilbakemeldingService, tilbakemeldingEnabled, cmsContentRetriever)
        );
    }

    private Component createTooltip() {
        return new WebMarkupContainer("tooltip")
                .add(new AttributeAppender("title", cmsContentRetriever.hentTekst("tooltip.hjelpetekst")))
                .add(new AttributeAppender("aria-label", cmsContentRetriever.hentTekst("skjult.label.hjelpetekst")));
    }

    private class IngenBehandlingerView extends WebMarkupContainer {

        public IngenBehandlingerView(String id, List<Henvendelsesbehandling> behandlinger) {
            super(id);

            add(visibleIf(isEmpty(behandlinger)));
            add(
                    new Label("ingenInnsendingerTittel", cmsContentRetriever.hentTekst("ingen.innsendinger.tittel")),
                    new Label("ingenInnsendingerTekst", cmsContentRetriever.hentTekst("ingen.innsendinger.tekst")),
                    new ExternalLink("skjemaerLink", cmsContentRetriever.hentTekst("ingen.innsendinger.link.skjemaer"), cmsContentRetriever.hentTekst("ingen.innsendinger.link.tekst.skjemaer")));
        }

    }
}
