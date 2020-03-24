package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider
import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.config.ServiceConfig
import no.nav.sbl.dialogarena.mininnboks.config.utils.JacksonConfig
import no.nav.sbl.dialogarena.types.Pingable
import no.nav.sbl.util.EnvironmentUtils.getRequiredProperty
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity

class PdlService(private val pdlClient: Client, private val stsService: SystemUserTokenProvider) {
    private val log = LoggerFactory.getLogger(PdlService::class.java)
    private val adressebeskyttelseQuery: String = lastQueryFraFil("hentAdressebeskyttelse")

    fun harKode6(fnr: String): Boolean = hentAdresseBeskyttelse(fnr) == PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG
    fun harKode7(fnr: String): Boolean = hentAdresseBeskyttelse(fnr) == PdlAdressebeskyttelseGradering.FORTROLIG
    fun harStrengtFortroligAdresse(fnr: String) = harKode6(fnr)
    fun harFortroligAdresse(fnr: String) = harKode7(fnr)

    fun hentAdresseBeskyttelse(fnr: String): PdlAdressebeskyttelseGradering? {
        return try {
            val response: PdlResponse = graphqlRequest(PdlRequest(adressebeskyttelseQuery, Variables(fnr)))
            val adressebeskyttelse: List<PdlAdressebeskyttelse> = response
                    ?.data
                    ?.hentPerson
                    ?.adressebeskyttelse
                    ?: emptyList()

            adressebeskyttelse
                    .map { it.gradering }
                    .firstOrNull()
        } catch (exception: Exception) {
            log.error("Kunne ikke utlede adressebeskyttelse, antar skjermet bruker", exception)
            null
        }
    }

    fun getHelsesjekk(): Pingable {
        val metadata = Pingable.Ping.PingMetadata(
                "pdl",
                getRequiredProperty(ServiceConfig.PDL_API_URL),
                "Henter adressebeskyttelse",
                false
        )

        return Pingable {
            try {
                val response = pdlClient.target(getRequiredProperty(ServiceConfig.PDL_API_URL))
                        .path("/graphql")
                        .request()
                        .options()

                if (response.status == 200) {
                    Pingable.Ping.lyktes(metadata)
                } else {
                    Pingable.Ping.feilet(metadata, "Statuskode: ${response.status}")
                }
            } catch (e: Exception) {
                Pingable.Ping.feilet(metadata, e)
            }
        }
    }

    private fun graphqlRequest(request: PdlRequest): PdlResponse {
        val uuid = UUID.randomUUID()
        try {
            val consumerOidcToken: String = stsService.token
            val veilederOidcToken: String = SubjectHandler.getSsoToken(SsoToken.Type.OIDC).orElseThrow { IllegalStateException("Kunne ikke hente ut veileders ssoTOken") }

            log.info("""
                    PDL-request: $uuid
                    ------------------------------------------------------------------------------------
                        callId: ${MDC.get(MDCConstants.MDC_CALL_ID)}
                    ------------------------------------------------------------------------------------
                """.trimIndent())

            val response = pdlClient.target(getRequiredProperty(ServiceConfig.PDL_API_URL))
                    .path("/graphql")
                    .request()
                    .header("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID))
                    .header("Nav-Consumer-Id", "mininnboks-api")
                    .header("Authorization", "Bearer $veilederOidcToken")
                    .header("Nav-Consumer-Token", "Bearer $consumerOidcToken")
                    .header("Tema", "GEN")
                    .post(Entity.json(request))

            val body = response.readEntity(String::class.java)
            log.info("""
                PDL-response: $uuid
                ------------------------------------------------------------------------------------
                    status: ${response.status} ${response.statusInfo}
                ------------------------------------------------------------------------------------
            """.trimIndent())

            val pdlResponse = JacksonConfig.mapper.readValue<PdlResponse>(body)

            if (pdlResponse.errors?.isNotEmpty() == true) {
                val errorMessages = pdlResponse.errors.map { it.message }.joinToString(", ")
                log.info(
                """
                    PDL-response: $uuid
                    ------------------------------------------------------------------------------------
                        status: ${response.status} ${response.statusInfo}
                        errors: $errorMessages
                    ------------------------------------------------------------------------------------
                """.trimIndent()
                )
                throw Exception(errorMessages)
            }

            return pdlResponse
        } catch (exception: Exception) {
            log.error("Feilet ved oppslag mot PDL (ID: $uuid)", exception)
            log.error("""
                    PDL-response: $uuid
                    ------------------------------------------------------------------------------------
                        exception:
                        $exception
                    ------------------------------------------------------------------------------------
                """.trimIndent())

            throw exception
        }
    }
}


private fun lastQueryFraFil(name: String): String {
    return PdlService::class.java
            .getResource("/pdl/$name.graphql")
            .readText()
            .replace("[\n\r]", "")
}
