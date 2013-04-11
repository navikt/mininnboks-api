package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.components.BehandlingPanel;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling.FERDIG;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling.NOT_HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling.UNDER_ARBEID;

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
        return "aktor";
    }

    private PropertyListView<Behandling> createFerdigView(final IModel<List<Behandling>> ferdig) {
        return new PropertyListView<Behandling>("behandlingerFerdig", ferdig) {

            @Override
            public void populateItem(final ListItem<Behandling> listItem) {
                Behandling behandling = listItem.getModelObject();
                IModel<List<Dokumentforventning>> dokModel = new ListModel<>(behandling.filterDokumenter(null, NOT_HOVEDSKJEMA));
                listItem.add(new BehandlingPanel("behandling", dokModel, behandling));
            }
        };
    }

    private PropertyListView<Behandling> createUnderArbeidView(final IModel<List<Behandling>> underArbeid) {
        return new PropertyListView<Behandling>("behandlingerUnderArbeid", underArbeid) {

            @Override
            public void populateItem(final ListItem<Behandling> listItem) {
                Behandling item = listItem.getModelObject();
                listItem.add(new Label("tittel", item.getTittel()));
                listItem.add(new Label("sistEndret", item.getSistEndret()));
                listItem.add(new Label("antallFerdige", item.getAntallInnsendteDokumenter()));
                listItem.add(new Label("antallTotalt", item.getAntallSubDokumenter()));
            }
        };
    }

    private static class BehandlingerLDM extends LoadableDetachableModel<List<Behandling>> {

        private IModel<List<Behandling>> parentModel;
        private final String status;

        BehandlingerLDM(IModel<List<Behandling>> parentModel, String status) {
            this.parentModel = parentModel;
            this.status = status;
        }

        @Override
        protected List<Behandling> load() {
            return on(parentModel.getObject()).filter(where(Behandling.BEHANDLING_STATUS, equalTo(status))).collect();
        }

        @Override
        public void detach() {
            super.detach();
            parentModel.detach();
        }
    }
}
