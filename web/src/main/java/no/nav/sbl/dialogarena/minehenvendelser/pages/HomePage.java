package no.nav.sbl.dialogarena.minehenvendelser.pages;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;

import java.util.List;

import javax.inject.Inject;

import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.components.BehandlingPanel;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.BehandlingService;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling.Behandlingsstatus;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Dokumentforventning;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.util.ListModel;

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
        add(createUnderArbeidView(new BehandlingerLDM(model, Behandlingsstatus.UNDER_ARBEID)), createFerdigView(new BehandlingerLDM(model,
                Behandlingsstatus.FERDIG)));
    }

    private String hentAktorId() {
        return "aktor";
    }

    private PropertyListView<Behandling> createFerdigView(final IModel<List<Behandling>> ferdig) {
        return new PropertyListView<Behandling>("behandlingerFerdig", ferdig) {

            @Override
            public void populateItem(final ListItem<Behandling> listItem) {
                Behandling behandling = listItem.getModelObject();
                IModel<List<Dokumentforventning>> dokModel = new ListModel<>(behandling.fetchAlleDokumenter());
                listItem.add(new BehandlingPanel("behandling", dokModel, behandling, innholdsTekster));
            }
        };
    }

    private PropertyListView<Behandling> createUnderArbeidView(final IModel<List<Behandling>> underArbeid) {
        return new PropertyListView<Behandling>("behandlingerUnderArbeid", underArbeid) {

            @Override
            public void populateItem(final ListItem<Behandling> listItem) {
                Behandling item = listItem.getModelObject();
                listItem.add(new Label("tittel", item.getTittel()));
                listItem.add(new Label("sistEndret", innholdsTekster.hentTekst("siste.endret") + " " + item.getSistEndret()));
                String antallDokumenter = innholdsTekster.hentTekst("antall.dokumenter");
                Label antallLabel = new Label("antall", String.format(antallDokumenter, item.getAntallInnsendteDokumenter(), item.getAntallSubDokumenter()));
                antallLabel.setEscapeModelStrings(true);
                listItem.add(antallLabel);
            }
        };
    }

    private static class BehandlingerLDM extends LoadableDetachableModel<List<Behandling>> {

        private IModel<List<Behandling>> parentModel;
        private final Behandlingsstatus status;

        BehandlingerLDM(IModel<List<Behandling>> parentModel, Behandlingsstatus status) {
            this.parentModel = parentModel;
            this.status = status;
        }

        @Override
        protected List<Behandling> load() {
            return on(parentModel.getObject()).filter(where(Behandling.STATUS, equalTo(status))).collect();
        }

        @Override
        public void detach() {
            super.detach();
            parentModel.detach();
        }
    }
}
