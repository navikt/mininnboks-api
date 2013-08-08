package no.nav.sbl.dialogarena.minehenvendelser.components.soeknader;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import org.apache.wicket.markup.html.list.ListItem;

import java.util.List;

public class SoeknaderUnderArbeidListView extends SoeknaderListView {

    public SoeknaderUnderArbeidListView(String id, List<? extends Soeknad> soeknader) {
        super(id, soeknader);
    }

    @Override
    protected void populateItem(ListItem<Soeknad> item) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
