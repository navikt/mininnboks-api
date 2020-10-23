package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse

object UbehandletMeldingUtils {
    fun hentUbehandledeMeldinger(henvendelser: List<Henvendelse?>): List<UbehandletMelding> {
        return henvendelser
                .sortedWith( Henvendelse.NYESTE_OVERST )
                .toSortedSet(compareBy { it?.traadId  })
                .filter { UbehandletMelding.erIkkeKassert(it)}
                .filter {UbehandletMelding.erUbesvart(it)  || UbehandletMelding.erUlest(it)}
                .map { UbehandletMelding(it!!) }
    }
}
