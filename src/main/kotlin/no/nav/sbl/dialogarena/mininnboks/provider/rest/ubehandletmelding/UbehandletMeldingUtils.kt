package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.provider.LinkService
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.UbehandletMelding.Companion.erUbesvart
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.UbehandletMelding.Companion.erUlest

object UbehandletMeldingUtils {
    fun hentUbehandledeMeldinger(henvendelser: List<Henvendelse>): List<UbehandletMelding> {
        return henvendelser
            .sortedWith(Henvendelse.NYESTE_OVERST)
            .toSortedSet(compareBy { it.traadId })
            .filter { UbehandletMelding.erIkkeKassert(it) }
            .filter { erUbesvart(it) || erUlest(it) }
            .map {
                UbehandletMelding(
                    behandlingskjedeId = it.traadId,
                    opprettetDato = it.opprettet,
                    type = it.type,
                    undertype = it.oppgaveType,
                    uri = LinkService.lagDirektelenkeTilMelding(it),
                    varselid = it.korrelasjonsId
                ).apply {
                    if (erUbesvart(it)) {
                        statuser.add(Status.UBESVART)
                    }
                    if (erUlest(it)) {
                        statuser.add(Status.ULEST)
                    }
                }
            }
    }
}
