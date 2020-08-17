package no.nav.sbl.dialogarena.mininnboks.consumer

import no.nav.common.auth.SubjectHandler
import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningPersonIkkeFunnet
import no.nav.tjeneste.virksomhet.person.v3.binding.HentGeografiskTilknytningSikkerhetsbegrensing
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import no.nav.tjeneste.virksomhet.person.v3.informasjon.GeografiskTilknytning
import no.nav.tjeneste.virksomhet.person.v3.informasjon.NorskIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Personidenter
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningRequest
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentGeografiskTilknytningResponse
import org.slf4j.LoggerFactory
import java.util.*
import javax.ws.rs.NotAuthorizedException

interface PersonService {
    fun hentGeografiskTilknytning(): Optional<String>
    class Default(private val personV3: PersonV3) : PersonService {
        override fun hentGeografiskTilknytning(): Optional<String> {
            val fnr = SubjectHandler.getIdent().orElseThrow { NotAuthorizedException("Fant ikke brukers OIDC-token") }
            val request = HentGeografiskTilknytningRequest().withAktoer(lagAktoer(fnr))
            val response: HentGeografiskTilknytningResponse
            response = try {
                personV3.hentGeografiskTilknytning(request)
            } catch (hentGeografiskTilknytningSikkerhetsbegrensing: HentGeografiskTilknytningSikkerhetsbegrensing) {
                logger.info("HentGeografiskTilknytningSikkerhetsbegrensing ved kall på hentGeografiskTilknyttning", hentGeografiskTilknytningSikkerhetsbegrensing)
                return Optional.empty()
            } catch (hentGeografiskTilknytningPersonIkkeFunnet: HentGeografiskTilknytningPersonIkkeFunnet) {
                logger.info("HentGeografiskTilknytningPersonIkkeFunnet ved kall på hentGeografiskTilknyttning", hentGeografiskTilknytningPersonIkkeFunnet)
                return Optional.empty()
            }
            return Optional.ofNullable(response)
                    .map { obj: HentGeografiskTilknytningResponse -> obj.geografiskTilknytning }
                    .map { obj: GeografiskTilknytning -> obj.geografiskTilknytning }
        }

        private fun lagAktoer(ident: String): PersonIdent {
            return PersonIdent().withIdent(NorskIdent().withType(personV3IdentType).withIdent(ident))
        }

        companion object {
            val logger = LoggerFactory.getLogger(PersonService::class.java)
            val personV3IdentType = Personidenter()
                    .withValue("FNR")
        }

    }
}
