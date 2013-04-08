package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandlig.BehandlingConsumer;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandlig.BehandlingDTO;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandlig.BehandlingDTO.Behandlingsstatus;


public class HomePage extends BasePage {

    @Inject
    private BehandlingConsumer behandlingConsumer;


    private String hentAktorId() {
        return "aktor";
    }

    public HomePage() {
        IModel<List<BehandlingDTO>> model = new LoadableDetachableModel<List<BehandlingDTO>>() {
            @Override
            protected List<BehandlingDTO> load() {
                return behandlingConsumer.hentBehandlinger(hentAktorId());
            }
        };

        IModel<List<BehandlingDTO>> underArbeid = new BehandlingerLDM(model, Behandlingsstatus.UNDER_ARBEID);
        IModel<List<BehandlingDTO>> ferdig = new BehandlingerLDM(model, Behandlingsstatus.FERDIG);

        add(createUnderArbeidView(underArbeid));
        add(createFerdigView(ferdig));
    }

    private PropertyListView<BehandlingDTO> createFerdigView(final IModel<List<BehandlingDTO>> ferdig) {
        return new PropertyListView<BehandlingDTO>("behandlingerFerdig", ferdig) {

            @Override
            public void populateItem(final ListItem<BehandlingDTO> listItem) {
                listItem.add(new Label("brukerBehandlingsId"));
            }
        };
    }

    private PropertyListView<BehandlingDTO> createUnderArbeidView(final IModel<List<BehandlingDTO>> underArbeid) {
        return new PropertyListView<BehandlingDTO>("behandlingerUnderArbeid", underArbeid) {

            @Override
            public void populateItem(final ListItem<BehandlingDTO> listItem) {
                listItem.add(new Label("brukerBehandlingsId"));
            }
        };
    }

    private static class BehandlingerLDM extends LoadableDetachableModel<List<BehandlingDTO>> {

        private IModel<List<BehandlingDTO>> parentModel;
        private final Behandlingsstatus status;

        BehandlingerLDM (IModel<List<BehandlingDTO>> parentModel, Behandlingsstatus status) {
            this.parentModel = parentModel;
            this.status = status;
        }

        @Override
        protected List<BehandlingDTO> load() {
            return on(parentModel.getObject()).filter(where(BehandlingDTO.STATUS, equalTo(status))).collect();
        }

        @Override
        public void detach() {
            super.detach();
            parentModel.detach();
        }
    }
}
