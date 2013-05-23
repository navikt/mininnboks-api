package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdService;
import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.components.BehandlingPanel;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.String.format;
import static java.lang.String.valueOf;
import static java.lang.System.getProperty;
import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus.FERDIG;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus.UNDER_ARBEID;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Dokumentbehandlingstatus.DOKUMENT_ETTERSENDING;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.STATUS;
import static org.apache.wicket.model.Model.of;

/**
 * Hovedside for applikasjonen. Laster inn behandlinger via. en service og
 * instansierer wicketmodellene med lister av innsendte og uferdige søknader
 */
public class HomePage extends BasePage {

    private static final String NULL_AKTOER_ID = "nullAktoer";
    @Inject
    protected Kodeverk kodeverkOppslag;
    @Inject
    private BehandlingService behandlingService;
    @Inject
    private AktoerIdService aktoerIdService;

    public HomePage(PageParameters pageParameters) {
        checkPageParametersAndSetAktoerId(pageParameters);
        IModel<List<Behandling>> model = createBehandlingerLDM();
        add(
                createTooltipLink(),
                createUnderArbeidView(new BehandlingerLDM(model, UNDER_ARBEID)),
                createFerdigView(new BehandlingerLDM(model, FERDIG)),
                new Label("hovedTittel", innholdstekster.hentTekst("hoved.tittel")),
                createIngenBehandlingerView(new BehandlingerLDM(model, UNDER_ARBEID)),
                new ExternalLink("forsiden", getProperty("inngangsporten.link.url"), innholdstekster.hentTekst("link.tekst.forsiden"))
        );
    }

    private WebMarkupContainer createTooltipLink() {
        WebMarkupContainer webMarkupContainer = new WebMarkupContainer("tooltip");
        webMarkupContainer.add(new AttributeAppender("title", of(innholdstekster.hentTekst("tooltip.tekst"))));
        return webMarkupContainer;
    }

    private LoadableDetachableModel<List<Behandling>> createBehandlingerLDM() {
        return new LoadableDetachableModel<List<Behandling>>() {
            @Override
            protected List<Behandling> load() {
                return behandlingService.hentBehandlinger(aktoerIdService.getAktoerId());
            }
        };
    }

    private void checkPageParametersAndSetAktoerId(PageParameters pageParameters) {
        if (pageParametersContainAktoerId(pageParameters)) {
            aktoerIdService.setAktoerId(valueOf(pageParameters.get("aktoerId")));
        } else {
            aktoerIdService.setAktoerId(NULL_AKTOER_ID);
        }
    }

    private boolean pageParametersContainAktoerId(PageParameters pageParameters) {
        return pageParameters.get("aktoerId") != null && !pageParameters.get("aktoerId").isEmpty();
    }

    private WebMarkupContainer createIngenBehandlingerView(BehandlingerLDM behandlinger) {
        WebMarkupContainer container = new WebMarkupContainer("ingenBehandlinger");
        container.add(
                new Label("ingenInnsendingerTittel", innholdstekster.hentTekst("ingen.innsendinger.tittel")),
                new Label("ingenInnsendingerTekst", innholdstekster.hentTekst("ingen.innsendinger.tekst")),
                new ExternalLink("skjemaerLink", "#", innholdstekster.hentTekst("ingen.innsendinger.link.tekst.skjemaer")));
        if (behandlinger.getTotalAntallBehandlinger() > 0) {
            container.setVisible(false);
        }
        return container;
    }

    private PropertyListView<Behandling> createFerdigView(final IModel<List<Behandling>> ferdig) {
        return new PropertyListView<Behandling>("behandlingerFerdig", ferdig) {

            @Override
            public void populateItem(final ListItem<Behandling> listItem) {
                if (listItem.getModelObject().getDokumentbehandlingstatus() == DOKUMENT_ETTERSENDING) {
                    listItem.add(new BehandlingPanel("behandling", new ListModel<>(listItem.getModelObject().fetchAlleUnntattHovedDokument()), listItem.getModelObject(), innholdstekster, kodeverkOppslag, DEFAULT_LOCALE));
                } else {
                    listItem.add(new BehandlingPanel("behandling", new ListModel<>(listItem.getModelObject().fetchAlleDokumenter()), listItem.getModelObject(), innholdstekster, kodeverkOppslag, DEFAULT_LOCALE));
                }

            }
        };
    }

    private PropertyListView<Behandling> createUnderArbeidView(final IModel<List<Behandling>> underArbeid) {
        return new PropertyListView<Behandling>("behandlingerUnderArbeid", underArbeid) {

            @Override
            public void populateItem(final ListItem<Behandling> listItem) {
                String dokumentInnsendingUrl = getProperty("dokumentinnsending.link.url") + listItem.getModelObject().getBehandlingsId() + "/" + aktoerIdService.getAktoerId();
                String formattedDate = new SimpleDateFormat("d. MMMM YYYY, HH:mm", DEFAULT_LOCALE).format(listItem.getModelObject().getSistEndret().toDate());
                listItem.add(
                        getTittel(listItem.getModelObject()),
                        createFormattedLabel("sistEndret", innholdstekster.hentTekst("behandling.siste.endret"), formattedDate),
                        new ExternalLink("fortsettLink", dokumentInnsendingUrl, innholdstekster.hentTekst("behandling.fortsett.innsending.link.tekst")));
            }

            private Label getTittel(Behandling item) {
                if (item.getDokumentbehandlingstatus() == DOKUMENT_ETTERSENDING) {
                    return createFormattedLabel("tittel", innholdstekster.hentTekst("behandling.ettersending.tekst"), kodeverkOppslag.getTittel(item.getKodeverkId()));
                }
                return new Label("tittel", kodeverkOppslag.getTittel(item.getKodeverkId()));
            }
        };
    }

    private Label createFormattedLabel(String wicketId, String unformattedText, Object... args) {
        return new Label(wicketId, format(unformattedText, args));
    }

    private static class BehandlingerLDM extends LoadableDetachableModel<List<Behandling>> {

        private final Behandlingsstatus status;
        private IModel<List<Behandling>> parentModel;

        BehandlingerLDM(IModel<List<Behandling>> parentModel, Behandlingsstatus status) {
            this.parentModel = parentModel;
            this.status = status;
        }

        @Override
        protected List<Behandling> load() {
            List<Behandling> behandlinger = new ArrayList<>(on(parentModel.getObject()).filter(where(STATUS, equalTo(status))).collect());
            sort(behandlinger, new Comparator<Behandling>() {
                @Override
                public int compare(Behandling o1, Behandling o2) {
                    if (UNDER_ARBEID == status) {
                        return o2.getSistEndret().compareTo(o1.getSistEndret());
                    }
                    return o2.getInnsendtDato().compareTo(o1.getInnsendtDato());
                }
            });
            return behandlinger;
        }

        @Override
        public void detach() {
            super.detach();
            parentModel.detach();
        }

        public int getTotalAntallBehandlinger() {
            return parentModel.getObject().size();
        }
    }
}
