package no.nav.sbl.dialogarena.minehenvendelser.components.soeknader;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.List;

public abstract class SoeknaderListView extends PropertyListView<Soeknad> {

    public SoeknaderListView(String id, final List<? extends Soeknad> soeknader) {
        super(id, new LoadableDetachableModel<List<? extends Soeknad>>() {

            @Override
            protected List<? extends Soeknad> load() {
                return soeknader;
            }

        });
    }

}
