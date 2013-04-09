package no.nav.sbl.dialogarena.minehenvendelser.components;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.henvendelse.behandling.jaxb.Behandling;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import java.util.List;

public class MultiBehandlingPanel extends Panel{

    public MultiBehandlingPanel(String id, IModel<List<Behandling>> model) {
        super(id, model);
    }

}
