package no.nav.sbl.dialogarena.mininnboks.innboks.utils;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;

import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SPORSMAL_SKRIFTLIG;

public class VisningUtils {

    public static String henvendelseStatusTekstKey(Henvendelse henvendelse) {
        String key;
        String henvendelsetypeIkkeSpesifik = forsteDelAvMeldingstype(henvendelse.type);
        if (henvendelse.type == SPORSMAL_SKRIFTLIG) {
            key = String.format("melding.status.%s", henvendelsetypeIkkeSpesifik);
        } else {
            key = String.format("melding.status.%s.%s", henvendelsetypeIkkeSpesifik, henvendelse.erLest() ? "lest" : "ulest");
        }
        return key.toLowerCase();
    }

    public static String forsteDelAvMeldingstype(Henvendelsetype henvendelsetype) {
        return henvendelsetype.name().substring(0, henvendelsetype.name().indexOf("_")).toLowerCase();
    }
}
