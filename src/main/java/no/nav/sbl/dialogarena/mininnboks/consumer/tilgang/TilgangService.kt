package no.nav.sbl.dialogarena.mininnboks.consumer.tilgang

import no.nav.sbl.dialogarena.mininnboks.Try
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService
import org.slf4j.LoggerFactory

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
    private val log = LoggerFactory.getLogger(TilgangService::class.java)

    override fun harTilgangTilKommunalInnsending(fnr: String): TilgangDTO {
        val harEnhet = Try.of { personService.hentEnhet().filter { it.isNotBlank() }.isPresent }
        if (harEnhet.isFailure()) {
            return TilgangDTO(TilgangDTO.Resultat.FEILET, "Kunne ikke hente brukers enhet: ${harEnhet.getFailure().message}")
        } else if (!harEnhet.get()) {
            return TilgangDTO(TilgangDTO.Resultat.INGEN_ENHET, "Bruker har ingen enhet")
        } else {
            log.info("Fant enhet for bruker: ${harEnhet.get()}")
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
