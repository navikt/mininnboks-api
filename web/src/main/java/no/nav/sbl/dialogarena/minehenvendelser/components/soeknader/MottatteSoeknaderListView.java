package no.nav.sbl.dialogarena.minehenvendelser.components.soeknader;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.basic.Label;


import java.util.List;

public class MottatteSoeknaderListView extends SoeknaderListView {

    public MottatteSoeknaderListView(String id, List<? extends Soeknad> soeknader) {
        super(id, soeknader);
    }

    @Override
    protected void populateItem(ListItem<Soeknad> item) {
        item.add(
                new Label("mottatt-tema", item.getModelObject().getTema()),
                new Label("mottatt-beskrivelse", item.getModelObject().getBeskrivelse())
        );
    }
}
