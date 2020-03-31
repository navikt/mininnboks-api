package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.config.ServiceConfig
import no.nav.sbl.dialogarena.mininnboks.config.utils.JacksonConfig
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import no.nav.sbl.dialogarena.types.Pingable
import no.nav.sbl.util.EnvironmentUtils.getRequiredProperty
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.lang.RuntimeException
import java.util.*
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity

interface PdlService {
    fun harKode6(fnr: String): Boolean
    fun harKode7(fnr: String): Boolean
    fun harStrengtFortroligAdresse(fnr: String): Boolean
    fun harFortroligAdresse(fnr: String): Boolean
    fun hentAdresseBeskyttelse(fnr: String): PdlAdressebeskyttelseGradering?
    fun getHelsesjekk(): Pingable
}

class PdlException(cause: Exception) : RuntimeException("Kunne ikke utlede adressebeskyttelse", cause)

class PdlServiceImpl(private val pdlClient: Client, private val stsService: SystemuserTokenProvider) : PdlService {
    private val log = LoggerFactory.getLogger(PdlService::class.java)
    private val adressebeskyttelseQuery: String = lastQueryFraFil("hentAdressebeskyttelse")

    override fun harStrengtFortroligAdresse(fnr: String) = harKode6(fnr)
    override fun harFortroligAdresse(fnr: String) = harKode7(fnr)
    override fun harKode6(fnr: String): Boolean = harGradering(fnr, PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG)
    override fun harKode7(fnr: String): Boolean = harGradering(fnr, PdlAdressebeskyttelseGradering.FORTROLIG)

    override fun hentAdresseBeskyttelse(fnr: String): PdlAdressebeskyttelseGradering? {
        return try {
            val response: PdlResponse = graphqlRequest(PdlRequest(adressebeskyttelseQuery, Variables(fnr)))
            response
                    .data
                    ?.hentPerson
                    ?.adressebeskyttelse
                    ?.firstOrNull()
                    ?.gradering
        } catch (exception: Exception) {
            log.error("Kunne ikke utlede adressebeskyttelse", exception)
            throw PdlException(exception)
        }
    }

    override fun getHelsesjekk(): Pingable {
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

    private fun harGradering(fnr: String, gradering: PdlAdressebeskyttelseGradering): Boolean {
        val pdlGradering = hentAdresseBeskyttelse(fnr)
        return gradering == pdlGradering
    }

    private fun graphqlRequest(request: PdlRequest): PdlResponse {
        val uuid = UUID.randomUUID()
        try {
            val veilederOidcToken: String = SubjectHandler.getSsoToken()
                    .map { it.token }
                    .orElseThrow { IllegalStateException("Kunne ikke hente ut bruker ssoTOken") }
            val consumerOidcToken: String = stsService.getSystemUserAccessToken() ?: throw IllegalStateException("Kunne ikke hente ut systemusertoken")

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
