package no.nav.sbl.dialogarena.minehenvendelser.components.soeknader;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;

import java.util.List;

public class FerdigeSoeknaderListView extends SoeknaderListView {

    public FerdigeSoeknaderListView(String id, List<? extends Soeknad> soeknader) {
        super(id, soeknader);
    }

    @Override
    protected void populateItem(ListItem<Soeknad> item) {
        item.add(
                new Label("ferdig-tema", item.getModelObject().getTema()),
                new Label("ferdig-beskrivelse", item.getModelObject().getBeskrivelse())
        );
    }

}
