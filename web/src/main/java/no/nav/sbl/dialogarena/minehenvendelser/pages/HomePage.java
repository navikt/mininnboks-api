package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.components.BehandlingPanel;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Dokumentforventning;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
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
    private BehandlingService behandlingService;

    public HomePage() {
        IModel<List<Behandling>> model = new LoadableDetachableModel<List<Behandling>>() {
            @Override
            protected List<Behandling> load() {
                return behandlingService.hentBehandlinger(hentAktorId());
            }
        };
        add(
                createUnderArbeidView(new BehandlingerLDM(model, UNDER_ARBEID)),
                createFerdigView(new BehandlingerLDM(model, FERDIG)));
    }

    private String hentAktorId() {
//        StringValue aktorId = getRequest().getQueryParameters().getParameterValue("aktorId");
//        return aktorId.toString();
        return "svein";
    }

    private PropertyListView<Behandling> createFerdigView(final IModel<List<Behandling>> ferdig) {
        return new PropertyListView<Behandling>("behandlingerFerdig", ferdig) {

            @Override
            public void populateItem(final ListItem<Behandling> listItem) {
                Behandling behandling = listItem.getModelObject();
                IModel<List<Dokumentforventning>> dokumentforventningListModel = new ListModel<>(behandling.fetchAlleDokumenter());
                listItem.add(new BehandlingPanel("behandling", dokumentforventningListModel, behandling, innholdstekster));
            }
        };
    }

    private PropertyListView<Behandling> createUnderArbeidView(final IModel<List<Behandling>> underArbeid) {
        return new PropertyListView<Behandling>("behandlingerUnderArbeid", underArbeid) {

            @Override
            public void populateItem(final ListItem<Behandling> listItem) {
                Behandling item = listItem.getModelObject();
                listItem.add(new Label("tittel", item.getTittel()));
                listItem.add(new Label("sistEndret", new StringResourceModel("siste.endret", this, null, item.getSistEndret().toDate())));
                listItem.add(new Label("antall", new StringResourceModel("antall.dokumenter", this, null, item.getAntallInnsendteDokumenter(), item.getAntallSubDokumenter())));
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
