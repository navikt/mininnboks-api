package no.nav.sbl.dialogarena.mininnboks.consumer.tilgang

import no.nav.sbl.dialogarena.mininnboks.Try
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService

data class TilgangDTO(val resultat: Resultat, val melding: String) {
    enum class Resultat {
        FEILET, KODE6, INGEN_ENHET, OK
    }
}

interface TilgangService {
    fun harTilgangTilKommunalInnsending(fnr: String): TilgangDTO
}

class TilgangServiceImpl(
        private val pdlService: PdlService,
        private val personService: PersonService
) : TilgangService {
    override fun harTilgangTilKommunalInnsending(fnr: String): TilgangDTO {
        val harEnhet = Try.of { personService.hentEnhet().isPresent }
        if (harEnhet.isFailure()) {
            return TilgangDTO(TilgangDTO.Resultat.FEILET, "Kunne ikke hente brukers enhet: ${harEnhet.getFailure().message}")
        } else if (!harEnhet.get()) {
            return TilgangDTO(TilgangDTO.Resultat.INGEN_ENHET, "Bruker har ingen enhet")
        }

        val harKode6 = Try.of { pdlService.harKode6(fnr) }
        if (harKode6.isFailure()) {
            return TilgangDTO(TilgangDTO.Resultat.FEILET, "Kunne ikke hente brukers diskresjonskode: ${harKode6.getFailure().message}")
        } else if (harKode6.get()) {
            return TilgangDTO(TilgangDTO.Resultat.KODE6, "Bruker har diskresjonskode")
        }

        return TilgangDTO(TilgangDTO.Resultat.OK, "")
    }

}
