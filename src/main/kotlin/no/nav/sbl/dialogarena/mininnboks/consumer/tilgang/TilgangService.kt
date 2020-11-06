package no.nav.sbl.dialogarena.mininnboks.consumer.tilgang

import no.nav.common.auth.subject.Subject
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService
import org.slf4j.LoggerFactory

data class TilgangDTO(val resultat: Resultat, val melding: String) {
    enum class Resultat {
        FEILET, KODE6, INGEN_ENHET, OK
    }
}

interface TilgangService {
    suspend fun harTilgangTilKommunalInnsending(subject: Subject): TilgangDTO
}

class TilgangServiceImpl(
        private val pdlService: PdlService,
        private val personService: PersonService
) : TilgangService {
    private val log = LoggerFactory.getLogger(TilgangService::class.java)

    override suspend fun harTilgangTilKommunalInnsending(subject: Subject): TilgangDTO {
        runCatching {
            sjekkGTPresent(subject)
        }.onSuccess {
            if (!it) {
                return TilgangDTO(TilgangDTO.Resultat.INGEN_ENHET, "Bruker har ikke gyldig GT")
            }
        }.onFailure {
            return TilgangDTO(TilgangDTO.Resultat.FEILET, "Kunne ikke hente brukers GT: ${it.message}")
        }

        runCatching {
            pdlService.harKode6(subject)
        }.onSuccess {
            if (it) {
                return TilgangDTO(TilgangDTO.Resultat.KODE6, "Bruker har diskresjonskode")
            }
        }.onFailure {
            return TilgangDTO(TilgangDTO.Resultat.FEILET, "Kunne ikke hente brukers diskresjonskode: ${it.message}")
        }
        return TilgangDTO(TilgangDTO.Resultat.OK, "")
    }

    private suspend fun sjekkGTPresent(subject: Subject): Boolean {
        val gt = personService.hentGeografiskTilknytning(subject)
                .filter { it.isNotBlank() }
                .filter { it.matches(Regex("\\d{4,}")) }
        log.info("PersonV3 (GT): ${gt.orElse("UKJENT/BLANK")}")
        return gt.isPresent
    }

}
