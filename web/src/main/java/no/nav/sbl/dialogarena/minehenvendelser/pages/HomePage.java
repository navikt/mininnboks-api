package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.sbl.dialogarena.minehenvendelser.AktoerIdService;
import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.components.BehandlingPanel;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus.FERDIG;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus.UNDER_ARBEID;
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

    public HomePage() {
        page = this;
        IModel<List<Behandling>> model = new LoadableDetachableModel<List<Behandling>>() {
            @Override
            protected List<Behandling> load() {
                return behandlingService.hentBehandlinger(aktoerIdService.getAktoerId());
            }
        };
        add(
                createUnderArbeidView(new BehandlingerLDM(model, UNDER_ARBEID)),
                createFerdigView(new BehandlingerLDM(model, FERDIG)) ,
                new Label("hovedTittel",new ResourceModel("hoved.tittel")));
    }

    private PropertyListView<Behandling> createFerdigView(final IModel<List<Behandling>> ferdig) {
        return new PropertyListView<Behandling>("behandlingerFerdig", ferdig) {

            @Override
            public void populateItem(final ListItem<Behandling> listItem) {
                Behandling behandling = listItem.getModelObject();
                IModel<List<Dokumentforventning>> dokumentforventningListModel = new ListModel<>(behandling.fetchAlleDokumenter());
                listItem.add(new BehandlingPanel("behandling", dokumentforventningListModel, behandling, innholdstekster, kodeverkOppslag));
            }
        };
    }

    private PropertyListView<Behandling> createUnderArbeidView(final IModel<List<Behandling>> underArbeid) {
        return new PropertyListView<Behandling>("behandlingerUnderArbeid", underArbeid) {

            @Override
            public void populateItem(final ListItem<Behandling> listItem) {
                Behandling item = listItem.getModelObject();
                listItem.add(getTittel(item));
                listItem.add(new Label("sistEndret", new StringResourceModel("siste.endret", page, null, item.getSistEndret().toDate())));
            }

            private Label getTittel(Behandling item) {
                if (item.getDokumentbehandlingstatus() == Behandling.Dokumentbehandlingstatus.ETTERSENDING) {
                    return new Label("tittel", new StringResourceModel("ettersending.tekst", page, null,null, kodeverkOppslag.hentKodeverk(item.getTittel())));
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
            Collections.sort(behandlinger, new Comparator<Behandling>() {
                @Override
                public int compare(Behandling o1, Behandling o2) {
                    if (status == UNDER_ARBEID) {
                        return o1.getSistEndret().compareTo(o2.getSistEndret());
                    }
                    return o1.getInnsendtDato().compareTo(o2.getInnsendtDato());
                }
            });
            return behandlinger;

        }

        @Override
        public void detach() {
            super.detach();
            parentModel.detach();
        }
    }
}
