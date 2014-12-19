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

    public static List<SporsmalVarsel> hentUlesteSporsmal(List<Henvendelse> henvendelser) {
        return varsler(henvendelser, ULEST_HENVENDELSE);
    }

    public static List<SporsmalVarsel> hentUbesvarteSporsmal(List<Henvendelse> henvendelser) {
        return varsler(henvendelser, UBESVART_HENVENDELSE);
    }

    private static List<SporsmalVarsel> varsler(List<Henvendelse> henvendelser, Predicate<Henvendelse> predicate) {
        HashMap<String, Henvendelse> nyesteHenvendelserITraad = new HashMap<>();

        for (Henvendelse henvendelse : on(henvendelser).collect(NYESTE_OVERST)) {
            if (!nyesteHenvendelserITraad.containsKey(henvendelse.traadId)) {
                nyesteHenvendelserITraad.put(henvendelse.traadId, henvendelse);
            }
        }

        return on(nyesteHenvendelserITraad.values()).filter(predicate).map(HENVENDELSE_TIL_SPORSMAL_VARSEL).collect();
    }

    private static final Transformer<Henvendelse, SporsmalVarsel> HENVENDELSE_TIL_SPORSMAL_VARSEL = new Transformer<Henvendelse, SporsmalVarsel>() {
        @Override
        public SporsmalVarsel transform(Henvendelse henvendelse) {
            return new SporsmalVarsel(henvendelse.traadId, henvendelse.opprettet.toDate(), henvendelse.type);
        }
    };

    private static final Predicate<Henvendelse> UBESVART_HENVENDELSE = new Predicate<Henvendelse>() {
        @Override
        public boolean evaluate(Henvendelse henvendelse) {
            return henvendelse.type == SPORSMAL_MODIA_UTGAAENDE;
        }
    };

    private static final Predicate<Henvendelse> ULEST_HENVENDELSE = new Predicate<Henvendelse>() {
        @Override
        public boolean evaluate(Henvendelse henvendelse) {
            return !henvendelse.erLest();
        }
    };
}
