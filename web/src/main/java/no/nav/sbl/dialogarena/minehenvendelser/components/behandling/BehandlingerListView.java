package no.nav.sbl.dialogarena.minehenvendelser.components.behandling;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.domain.Henvendelsesbehandling;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.LoadableDetachableModel;

import javax.inject.Inject;
import java.util.List;

public abstract class BehandlingerListView extends PropertyListView<Henvendelsesbehandling> {

    @Inject
    protected CmsContentRetriever innholdstekster;

    @Inject
    protected Kodeverk kodeverk;

    public BehandlingerListView(String id, final List<? extends Henvendelsesbehandling> behandlinger) {
        super(id, new LoadableDetachableModel<List<? extends Henvendelsesbehandling>>() {
            @Override
            protected List<? extends Henvendelsesbehandling> load() {
                return behandlinger;
            }
        });
    }

}
