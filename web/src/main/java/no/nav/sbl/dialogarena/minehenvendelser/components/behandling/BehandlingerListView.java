package no.nav.sbl.dialogarena.minehenvendelser.components.behandling;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Behandling;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.util.List;

public abstract class BehandlingerListView extends PropertyListView<Behandling> {

    @Inject
    protected CmsContentRetriever innholdstekster;

    @Inject
    protected Kodeverk kodeverk;

    public BehandlingerListView(String id, final List<? extends Behandling> behandlinger) {
        super(id, new LoadableDetachableModel<List<? extends Behandling>>() {
            @Override
            protected List<? extends Behandling> load() {
                return behandlinger;
            }
        });
    }

}
