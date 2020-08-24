package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import java.util.*
import java.util.function.Supplier
import java.util.stream.Collectors

object UbehandletMeldingUtils {
    var supplier = Supplier<Set<Henvendelse>> { TreeSet(Comparator.comparing { h: Henvendelse -> h.traadId!! }) }
    fun hentUbehandledeMeldinger(henvendelser: List<Henvendelse?>): List<UbehandletMelding> {
        return henvendelser
                .stream()
                .sorted(Henvendelse.NYESTE_OVERST)
                .collect(Collectors.toCollection(supplier))
                .stream()
                .filter(UbehandletMelding.erIkkeKassert)
                .filter(UbehandletMelding.erUbesvart.or(UbehandletMelding.erUlest))
                .map { henvendelse: Henvendelse? -> UbehandletMelding(henvendelse!!) }
                .collect(Collectors.toList())
    }
}
