package no.nav.sbl.dialogarena.mininnboks.consumer.tilgang

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import no.nav.common.auth.subject.Subject
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.Adresse
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService
import org.slf4j.LoggerFactory
import java.util.regex.Pattern.matches

data class TilgangDTO(val resultat: Resultat, val melding: String) {
    enum class Resultat {
        FEILET, KODE6, INGEN_ENHET, OK
    }
}
sealed class AdresseDTO(val fantAdresse: Boolean) {
    class FantAdresse(val adresse: Adresse) : AdresseDTO(true)
    object IngenAdresse : AdresseDTO(false)

    companion object {
        fun fromAdresse(adresse: Adresse?): AdresseDTO {
            return if (adresse == null) {
                IngenAdresse
            } else {
                FantAdresse(adresse)
            }
        }
    }
}

interface TilgangService {
    suspend fun harTilgangTilKommunalInnsending(subject: Subject): TilgangDTO
    suspend fun hentFolkeregistrertAdresseMedGt(subject: Subject): AdresseDTO
}

class TilgangServiceImpl(
        private val pdlService: PdlService,
        private val personService: PersonService
) : TilgangService {
    private val log = LoggerFactory.getLogger(TilgangService::class.java)

    override suspend fun harTilgangTilKommunalInnsending(subject: Subject): TilgangDTO {
        val harGt = runCatching {
            val tilKnytting = personService.hentGeografiskTilknytning(subject)
            return@runCatching tilKnytting?.isNotBlank()?.and(matches("\\d{4,}", tilKnytting)) ?: false
        }

        if (harGt.isFailure) {
            return TilgangDTO(TilgangDTO.Resultat.FEILET, "Kunne ikke hente brukers GT: ${harGt.exceptionOrNull()?.message}")
        } else if (!harGt.getOrThrow()) {
            return TilgangDTO(TilgangDTO.Resultat.INGEN_ENHET, "Bruker har ikke gyldig GT")
        }

        val harKode6 = runCatching {
            pdlService.harKode6(subject)
        }
        if (harKode6.isFailure) {
            return TilgangDTO(TilgangDTO.Resultat.FEILET, "Kunne ikke hente brukers diskresjonskode: ${harKode6.exceptionOrNull()?.message}")
        } else if (harKode6.getOrNull()!!) {
            return TilgangDTO(TilgangDTO.Resultat.KODE6, "Bruker har diskresjonskode")
        }
        return TilgangDTO(TilgangDTO.Resultat.OK, "")
    }

    override suspend fun hentFolkeregistrertAdresseMedGt(subject: Subject): AdresseDTO = coroutineScope {
        val gtAsync = async { pdlService.hentGeografiskTilknytning(subject) }
        val adresserAsync = async { pdlService.hentFolkeregistrertAdresse(subject) }

        val gt = gtAsync.await()
        val adresser = adresserAsync.await()

        val adresseMedGt = adresser?.copy( geografiskTilknytning = gt )
        AdresseDTO.fromAdresse(adresseMedGt)
    }
}
