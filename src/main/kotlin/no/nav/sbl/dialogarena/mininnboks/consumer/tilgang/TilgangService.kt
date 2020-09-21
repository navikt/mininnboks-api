package no.nav.sbl.dialogarena.mininnboks.consumer.tilgang

import no.nav.common.auth.subject.Subject
import no.nav.sbl.dialogarena.mininnboks.Try
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService
import no.nav.sbl.dialogarena.mininnboks.externalCall
import org.slf4j.LoggerFactory

data class TilgangDTO(val resultat: Resultat, val melding: String) {
    enum class Resultat {
        FEILET, KODE6, INGEN_ENHET, OK
    }
}

interface TilgangService {
    suspend fun  harTilgangTilKommunalInnsending(subject: Subject): TilgangDTO
}

class TilgangServiceImpl(
        private val pdlService: PdlService,
        private val personService: PersonService
) : TilgangService {
    private val log = LoggerFactory.getLogger(TilgangService::class.java)

    override suspend fun harTilgangTilKommunalInnsending(subject: Subject): TilgangDTO {
        val harGT = Try.of {
            val gt = personService.hentGeografiskTilknytning(subject)
                    .filter { it.isNotBlank() }
                    .filter { it.matches(Regex("\\d{4,}")) }
            log.info("PersonV3 (GT): ${gt.orElse("UKJENT/BLANK")}")
                gt.isPresent

            }

        if (harGT.isFailure()) {
            return TilgangDTO(TilgangDTO.Resultat.FEILET, "Kunne ikke hente brukers GT: ${harGT.getFailure().message}")
        } else if (!harGT.get()) {
            return TilgangDTO(TilgangDTO.Resultat.INGEN_ENHET, "Bruker har ikke gyldig GT")
        }

        val harKode6 = Try.of {
            pdlService.harKode6(subject) }
        if (harKode6.isFailure()) {
            return TilgangDTO(TilgangDTO.Resultat.FEILET, "Kunne ikke hente brukers diskresjonskode: ${harKode6.getFailure().message}")
        } else if (harKode6.get()) {
            return TilgangDTO(TilgangDTO.Resultat.KODE6, "Bruker har diskresjonskode")
        }

        return TilgangDTO(TilgangDTO.Resultat.OK, "")
    }

}
