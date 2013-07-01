package no.nav.sbl.dialogarena.minehenvendelser.pages;

import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.HenvendelseSporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesporsmalogsvar.v1.meldinger.OpprettSporsmalRequest;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
import java.io.Serializable;

/**
 *  Gir bruker mulighet til å sende inn spørsmål til NAV
 */
public class SporsmalSide extends BasePage {

    @Inject
    private HenvendelseSporsmalOgSvarPortType sporsmalOgSvarService;


    public SporsmalSide() {
        add(new SporsmalForm("sporsmalForm", new CompoundPropertyModel<>(new Sporsmal())));
    }

    private class SporsmalForm extends Form<Sporsmal> {

        private SporsmalForm(String id, IModel model) {
            super(id, model);
            TextArea textArea = new TextArea("sporsmal");
            add(textArea);
        }

        @Override
        protected void onSubmit() {
            sporsmalOgSvarService.opprettSporsmal(new OpprettSporsmalRequest().
                    withSporsmal(this.getModelObject().getSporsmal()).withTema("MittTema"));
        }
    }

    private static class Sporsmal implements Serializable {
        private String sporsmal;

        private String getSporsmal() {
            return sporsmal;
        }

        private void setSporsmal(String sporsmal) {
            this.sporsmal = sporsmal;
        }
    }

}
