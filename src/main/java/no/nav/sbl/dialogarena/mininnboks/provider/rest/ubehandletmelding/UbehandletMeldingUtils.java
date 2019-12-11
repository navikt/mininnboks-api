package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse.NYESTE_OVERST;
import static no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.UbehandletMelding.*;

public class UbehandletMeldingUtils {
    static Supplier<Set<Henvendelse>> supplier = () -> new TreeSet<>(Comparator.comparing(h -> h.traadId));

    public static List<UbehandletMelding> hentUbehandledeMeldinger(List<Henvendelse> henvendelser) {

        return henvendelser
                .stream()
                .sorted(NYESTE_OVERST)
                .collect(Collectors.toCollection(supplier))
                .stream()
                .filter(erIkkeKassert)
                .filter(erUbesvart.or(erUlest))
                .map(UbehandletMelding::new)
                .collect(toList());
    }
}
