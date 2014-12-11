package no.nav.sbl.dialogarena.mininnboks.innboks.utils;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import org.apache.wicket.model.ResourceModel;

import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.FRA_NAV;

public class VisningUtils {
    public static String henvendelseStatusTekst(Henvendelse henvendelse) {
        if (henvendelse.type == null) {
            return "Fant ingen status";
        }
        ResourceModel rm = new ResourceModel(henvendelseStatusTekstKey(henvendelse));
        return rm.getObject();
    }

    private static String henvendelseStatusTekstKey(Henvendelse henvendelse) {
        String key = String.format("henvendelse.status.%s", henvendelse.type.name());
        if (FRA_NAV.contains(henvendelse.type)) {
            key += String.format(".%s", henvendelse.erLest() ? "lest" : "ulest");
        }
        return key;
    }
}
