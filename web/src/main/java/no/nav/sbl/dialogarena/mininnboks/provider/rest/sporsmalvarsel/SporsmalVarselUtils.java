package no.nav.sbl.dialogarena.mininnboks.provider.rest.sporsmalvarsel;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import java.util.HashMap;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.NYESTE_OVERST;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE;

public class SporsmalVarselUtils {

    public static List<SporsmalVarsel> hentUbehandledeSporsmal(List<Henvendelse> henvendelser) {
        HashMap<String, Henvendelse> nyesteHenvendelserITraad = new HashMap<>();

        for (Henvendelse henvendelse : on(henvendelser).collect(NYESTE_OVERST)) {
            if (!nyesteHenvendelserITraad.containsKey(henvendelse.traadId)) {
                nyesteHenvendelserITraad.put(henvendelse.traadId, henvendelse);
            }
        }

        return on(nyesteHenvendelserITraad.values()).filter(ULEST_ELLER_UBESVART).map(TIL_SPORSMAL_VARSEL).collect();
    }

    private static final Transformer<Henvendelse, SporsmalVarsel> TIL_SPORSMAL_VARSEL = new Transformer<Henvendelse, SporsmalVarsel>() {
        @Override
        public SporsmalVarsel transform(Henvendelse henvendelse) {
            return new SporsmalVarsel(henvendelse);
        }
    };

    private static final Predicate<Henvendelse> ULEST_ELLER_UBESVART = new Predicate<Henvendelse>() {
        @Override
        public boolean evaluate(Henvendelse henvendelse) {
            return erUlest(henvendelse) || erUbesvart(henvendelse);
        }
    };

    public static boolean erUbesvart(Henvendelse henvendelse) {
        return henvendelse.type == SPORSMAL_MODIA_UTGAAENDE;
    }

    public static boolean erUlest(Henvendelse henvendelse) {
        return !henvendelse.erLest();
    }
}
