package no.nav.sbl.dialogarena.minehenvendelser.sporsmal.tema;

import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.Sporsmal;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.Stegnavigator;
import org.apache.wicket.model.Model;

public class VelgTemaPage extends BasePage {
    public VelgTemaPage() {
        add(new VelgTemaPanel("tema", Model.of(new Sporsmal()), new Stegnavigator() {
            @Override
            public void neste() {

            }
        }));
    }
}
