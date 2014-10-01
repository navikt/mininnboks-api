package no.nav.sbl.dialogarena.mininnboks.innboks.utils;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

public class KassertInnholdUtils {

    public static final String INNHOLD_KASSERT_KEY = "innhold.kassert";
    public static final String TEMAGRUPPE_UKJENT_KEY = "temagruppe.kassert";

    public static IModel<String> getFritekstModel(Henvendelse henvendelse) {
        if (henvendelse.fritekst != null) {
            return Model.of(henvendelse.fritekst);
        } else {
            return new ResourceModel(INNHOLD_KASSERT_KEY);
        }
    }

    public static String henvendelseTemagruppeKey(Henvendelse henvendelse) {
        return henvendelse.temagruppe != null ? henvendelse.temagruppe.name() : TEMAGRUPPE_UKJENT_KEY;
    }
}

