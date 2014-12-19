package no.nav.sbl.dialogarena.mininnboks.innboks.utils;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public abstract class KassertInnholdUtils {

    public static final String INNHOLD_KASSERT_KEY = "innhold.kassert";
    public static final String TEMAGRUPPE_UKJENT_KEY = "temagruppe.kassert";

    public static IModel<String> getFritekstModel(final IModel<Henvendelse> henvendelse) {
        if (henvendelse.getObject().fritekst != null) {
            return new AbstractReadOnlyModel<String>() {
                @Override
                public String getObject() {
                    return henvendelse.getObject().fritekst;
                }
            };
        } else {
            return new ResourceModel(INNHOLD_KASSERT_KEY);
        }
    }

    public static String henvendelseTemagruppeKey(Temagruppe temagruppe) {
        return temagruppe != null ? temagruppe.name() : TEMAGRUPPE_UKJENT_KEY;
    }
}

