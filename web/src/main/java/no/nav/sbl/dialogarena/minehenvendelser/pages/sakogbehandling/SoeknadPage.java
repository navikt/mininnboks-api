package no.nav.sbl.dialogarena.minehenvendelser.pages.sakogbehandling;

import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.sakogbehandling.domain.Soeknad;
import org.apache.wicket.markup.html.basic.Label;

public class SoeknadPage extends BasePage {

    public SoeknadPage(Soeknad soeknad) {
        add(
                new Label("overskrift", soeknad.getTema()),
                new Label("beskrivelse", soeknad.getBeskrivelse()),
                new Label("behandlingstid", soeknad.getNormertBehandlingsTid())
        );
    }

}
