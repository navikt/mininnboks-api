package no.nav.sbl.dialogarena.minehenvendelser.pages;

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
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        final List<WSSporsmalOgSvar> sporsmalOgSvar = sporsmalOgSvarService.hentSporsmalOgSvarListe("***REMOVED***");

        ListModel<Sporsmal> sporsmalListeModell = new ListModel<>(sporsmal);
        for (WSSporsmalOgSvar ss : sporsmalOgSvar) {
            Sporsmal s = new Sporsmal();
            s.sporsmalString = ss.getSporsmal();
            s.svar = ss.getSvar();
            sporsmal.add(s);
        }
        PropertyListView<Sporsmal> liste = new PropertyListView<Sporsmal>("sporsmalliste", sporsmalListeModell) {
            @Override
            protected void populateItem(ListItem<Sporsmal> item) {
                item.add(new Label("sporsmalString", item.getModelObject().sporsmalString));
                item.add(new Label("svar", item.getModelObject().svar));
            }
        };
        add(liste);
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
