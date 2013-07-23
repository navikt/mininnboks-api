package no.nav.sbl.dialogarena.minehenvendelser.components.behandling;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.minehenvendelser.ApplicationConstants.DEFAULT_LOCALE;
import static no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.STATUS;

import java.util.Comparator;
import java.util.List;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling.Behandlingsstatus;

import org.apache.wicket.markup.html.list.ListItem;

public class FerdigeBehandlingerListView extends BehandlingerListView {

    public FerdigeBehandlingerListView(String id, List<? extends Behandling> behandlinger) {
        super(id, filterAndSort(behandlinger));
    }

    private static List<? extends Behandling> filterAndSort(List<? extends Behandling> behandlinger) {
        return on(behandlinger).filter(where(STATUS, equalTo(Behandlingsstatus.FERDIG))).collect(new Comparator<Behandling>() {
            @Override
            public int compare(Behandling arg0, Behandling arg1) {
                return arg1.getInnsendtDato().compareTo(arg0.getInnsendtDato());
            }
        });
    }

    @Override
    protected void populateItem(ListItem<Behandling> item) {
        item.add(new BehandlingPanel(item.getModelObject(), innholdstekster, kodeverk, DEFAULT_LOCALE));
    }

}
