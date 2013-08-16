package no.nav.sbl.dialogarena.minehenvendelser.components.soeknader;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import no.nav.sbl.dialogarena.minehenvendelser.pages.sakogbehandling.SoeknadPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.link.Link;

import java.util.List;

public class SoeknaderUnderArbeidListView extends SoeknaderListView {

    public SoeknaderUnderArbeidListView(String id, List<? extends Soeknad> soeknader) {
        super(id, soeknader);
    }

    @Override
    protected void populateItem(final ListItem<Soeknad> item) {
        item.add(
                new Label("ua-tema", item.getModelObject().getTema()),
                new Label("ua-beskrivelse", item.getModelObject().getBeskrivelse()),
                new Link("ua-detaljer") {
                    @Override
                    public void onClick() {
                        setResponsePage(new SoeknadPage(item.getModelObject()));
                    }
                }
        );
    }
}
