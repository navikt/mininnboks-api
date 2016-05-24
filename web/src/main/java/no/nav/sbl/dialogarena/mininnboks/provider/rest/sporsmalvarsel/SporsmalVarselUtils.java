package no.nav.sbl.dialogarena.mininnboks.provider.rest.sporsmalvarsel;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.NYESTE_OVERST;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE;

public class SporsmalVarselUtils {

    public static List<SporsmalVarsel> hentUbehandledeSporsmal(List<Henvendelse> henvendelser) {
        HashMap<String, Henvendelse> nyesteHenvendelserITraad = new HashMap<>();

        henvendelser.stream().sorted(NYESTE_OVERST).collect(toList()).stream()
                .forEach(henvendelse -> {
                    if (!nyesteHenvendelserITraad.containsKey(henvendelse.traadId)) {
                        nyesteHenvendelserITraad.put(henvendelse.traadId, henvendelse);
                    }
                });

        return nyesteHenvendelserITraad.values().stream()
                .filter(ULEST_ELLER_UBESVART)
                .map(TIL_SPORSMAL_VARSEL)
                .collect(toList());
    }

    private static final Function<Henvendelse, SporsmalVarsel> TIL_SPORSMAL_VARSEL = henvendelse -> new SporsmalVarsel(henvendelse);

    private static final Predicate<Henvendelse> ULEST_ELLER_UBESVART = henvendelse -> erUlest(henvendelse) || erUbesvart(henvendelse);

    public static boolean erUbesvart(Henvendelse henvendelse) {
        return henvendelse.type == SPORSMAL_MODIA_UTGAAENDE;
    }

    public static boolean erUlest(Henvendelse henvendelse) {
        return !henvendelse.isLest();
    }
}
