package no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe;

import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.Select2Choice;
import com.vaynberg.wicket.select2.TextChoiceProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import java.util.Collection;

import static java.util.Arrays.asList;

public class TemagruppeDropdown extends Select2Choice<Temagruppe> {

    public TemagruppeDropdown(String id, IModel<Temagruppe> model) {
        super(id, model, new TemagruppeProvider());

        getSettings().setMinimumResultsForSearch(-1);
        getSettings().setContainerCssClass("temagruppevalg");
        getSettings().setWidth("365px");
    }

    static class TemagruppeProvider extends TextChoiceProvider<Temagruppe> {
        @Override
        protected String getDisplayText(Temagruppe choice) {
            return new ResourceModel(choice.toString()).getObject();
        }

        @Override
        protected Object getId(Temagruppe choice) {
            return choice.name();
        }

        @Override
        public void query(String term, int page, Response<Temagruppe> response) {
            response.addAll(asList(Temagruppe.values()));
        }

        @Override
        public Collection<Temagruppe> toChoices(Collection<String> ids) {
            for (Temagruppe temagruppe : Temagruppe.values()) {
                if (ids.contains(temagruppe.name())) {
                    return asList(temagruppe);
                }
            }
            throw new RuntimeException("Teamgruppe velger feilet");
        }
    }
}
