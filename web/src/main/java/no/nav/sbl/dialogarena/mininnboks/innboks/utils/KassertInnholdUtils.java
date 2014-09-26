package no.nav.sbl.dialogarena.mininnboks.innboks.utils;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;

public class KassertInnholdUtils {

    private static final String INNHOLD_KASSERT_KEY = "innhold.kassert";

    public static IModel<String> getFritekstModel(Henvendelse henvendelse) {
        if (henvendelse.fritekst != null) {
            return Model.of(henvendelse.fritekst);
        } else {
            return new ResourceModel(INNHOLD_KASSERT_KEY);
        }
    }

    public static class TemagruppeModel extends StringResourceModel {

        private static final String TEMAGRUPPE_UKJENT_KEY = "temagruppe.kassert";

        public TemagruppeModel(String resourceKey, Henvendelse henvendelse) {
            super(resourceKey, null, new Object[]{
                    new ResourceModel(henvendelse.type.name()),
                    new ResourceModel(henvendelse.temagruppe != null ? henvendelse.temagruppe.name() : TEMAGRUPPE_UKJENT_KEY)
            });
        }
    }
}

