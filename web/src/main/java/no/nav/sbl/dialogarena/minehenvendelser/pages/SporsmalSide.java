package no.nav.sbl.dialogarena.minehenvendelser.pages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmalOgSvar;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import static java.util.Arrays.asList;

/**
 *  Gir bruker mulighet til å sende inn spørsmål til NAV
 */
public class SporsmalSide extends BasePage {

    @Inject
    private SporsmalOgSvarPortType sporsmalOgSvarService;

    final List<Sporsmal> sporsmal = new ArrayList<>();

    final List<String> tema = asList("Uføre", "Sykepenger", "Tjenestebasert innskuddspensjon", "Annet");

    public SporsmalSide() {
        add(new SporsmalForm("sporsmalForm", new CompoundPropertyModel<>(new Sporsmal())));

        PropertyListView<WSSporsmalOgSvar> liste = new PropertyListView<WSSporsmalOgSvar>("sporsmalliste", new AlleSporsmalOgSvar()) {
            @Override
            protected void populateItem(ListItem<WSSporsmalOgSvar> item) {
                item.add(new Label("sporsmalString", item.getModelObject().getSporsmal()));
                item.add(new Label("svar", item.getModelObject().getSvar()));
            }
        };
        add(liste);
    }

    private class AlleSporsmalOgSvar extends LoadableDetachableModel<List<WSSporsmalOgSvar>> {
        @Override
        protected List<WSSporsmalOgSvar> load() {
            return sporsmalOgSvarService.hentSporsmalOgSvarListe("***REMOVED***");
        }
    }

    private final class SporsmalForm extends Form<Sporsmal> {

        private SporsmalForm(String id, IModel model) {
            super(id, model);
            TextArea textArea = new TextArea("sporsmalString");
            DropDownChoice<String> temavelger = new DropDownChoice<>("tema", tema);
            temavelger.setMarkupId("tema");
            add(textArea, temavelger);
        }

        @Override
        protected void onSubmit() {
            Sporsmal innsendt = this.getModelObject();
            sporsmalOgSvarService.opprettSporsmal(new WSSporsmal().withFritekst(innsendt.sporsmalString).withTema(innsendt.tema));
                    sporsmal.add(this.getModelObject());
        }
    }

    private static class Sporsmal implements Serializable {
        public String sporsmalString, svar, tema;
    }

}
