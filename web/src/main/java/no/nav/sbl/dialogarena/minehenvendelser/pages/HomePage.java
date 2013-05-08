package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdService;
import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.components.BehandlingPanel;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkService;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus.FERDIG;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus.UNDER_ARBEID;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Dokumentbehandlingstatus.ETTERSENDING;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.STATUS;

/**
 * Hovedside for applikasjonen. Laster inn behandlinger via. en service og
 * instansierer wicketmodellene med lister av innsendte og uferdige søknader
 */
public class HomePage extends BasePage {

    @Inject
    protected KodeverkService kodeverkOppslag;
    @Inject
    private BehandlingService behandlingService;
    @Inject
    private AktoerIdService aktoerIdService;
    private HomePage page;

    public HomePage(PageParameters pageParameters) {
        page = this;
        checkAktoerId(pageParameters);
        IModel<List<Behandling>> model = createBehandlingerLDM();
        add(
                createUnderArbeidView(new BehandlingerLDM(model, UNDER_ARBEID)),
                createFerdigView(new BehandlingerLDM(model, FERDIG)),
                new Label("hovedTittel", new ResourceModel("hoved.tittel")),
                createIngenBehandlingerView(new BehandlingerLDM(model, UNDER_ARBEID))
        );
    }

    private LoadableDetachableModel<List<Behandling>> createBehandlingerLDM() {
        return new LoadableDetachableModel<List<Behandling>>() {
            @Override
            protected List<Behandling> load() {
                return behandlingService.hentBehandlinger(aktoerIdService.getAktoerId());
            }
        };
    }

    private void checkAktoerId(PageParameters pageParameters) {
        if (pageParameters.get("aktoerId") != null) {
            aktoerIdService.setAktoerId(valueOf(pageParameters.get("aktoerId")));
        }
    }

    private WebMarkupContainer createIngenBehandlingerView(BehandlingerLDM behandlinger) {
        WebMarkupContainer container = new WebMarkupContainer("ingenBehandlinger");
        container.add(
                new Label("ingenInnsendingerTittel", new ResourceModel("ingen.innsendinger.tittel")),
                new Label("ingenInnsendingerTekst", new ResourceModel("ingen.innsendinger.tekst")));
        if (behandlinger.getTotalAntallBehandlinger() > 0) {
            container.setVisible(false);
        }
        return container;
    }

    private PropertyListView<Behandling> createFerdigView(final IModel<List<Behandling>> ferdig) {
        return new PropertyListView<Behandling>("behandlingerFerdig", ferdig) {

            @Override
            public void populateItem(final ListItem<Behandling> listItem) {
                if (listItem.getModelObject().getDokumentbehandlingstatus() == ETTERSENDING) {
                    listItem.add(new BehandlingPanel("behandling", new ListModel<>(listItem.getModelObject().fetchAlleUnntattHovedDokument()), listItem.getModelObject(), innholdstekster, kodeverkOppslag));
                } else {
                    listItem.add(new BehandlingPanel("behandling", new ListModel<>(listItem.getModelObject().fetchAlleDokumenter()), listItem.getModelObject(), innholdstekster, kodeverkOppslag));
                }

            }
        };
    }

    private PropertyListView<Behandling> createUnderArbeidView(final IModel<List<Behandling>> underArbeid) {
        return new PropertyListView<Behandling>("behandlingerUnderArbeid", underArbeid) {

            @Override
            public void populateItem(final ListItem<Behandling> listItem) {
                String dokumentInnsendingUrl = dokumentInnsendingBaseUrl + "oversikt/" + listItem.getModelObject().getBehandlingsId();
                listItem.add(
                        getTittel(listItem.getModelObject()),
                        new Label("sistEndret", new StringResourceModel("siste.endret", page, null, listItem.getModelObject().getSistEndret().toDate())),
                        new ExternalLink("fortsettLink", dokumentInnsendingUrl, "Fortsett/slett innsending"));
            }

            private Label getTittel(Behandling item) {
                if (item.getDokumentbehandlingstatus() == ETTERSENDING) {
                    return new Label("tittel", new StringResourceModel("ettersending.tekst", page, null, null, kodeverkOppslag.hentKodeverk(item.getTittel())));
                }
                return new Label("tittel", kodeverkOppslag.hentKodeverk(item.getTittel()));
            }
        };
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
                    if (status == UNDER_ARBEID) {
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
