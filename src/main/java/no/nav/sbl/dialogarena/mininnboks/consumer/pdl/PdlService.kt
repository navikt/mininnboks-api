package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestUtils.parseJsonResponseOrThrow
import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.config.utils.JacksonConfig
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*
import no.nav.common.health.HealthCheck;
import no.nav.common.health.HealthCheckResult
import no.nav.common.rest.client.RestUtils.toJsonRequestBody
import okhttp3.*
import javax.ws.rs.client.Entity

/*interface PdlService {
    fun harKode6(fnr: String): Boolean
    fun harKode7(fnr: String): Boolean
    fun harStrengtFortroligAdresse(fnr: String): Boolean
    fun harFortroligAdresse(fnr: String): Boolean
    fun hentAdresseBeskyttelse(fnr: String): PdlAdressebeskyttelseGradering?
    fun getHelsesjekk(): Pingable
}*/

class PdlException(cause: Exception) : RuntimeException("Kunne ikke utlede adressebeskyttelse", cause)

open class PdlService(private val pdlClient: OkHttpClient,
                      private val stsService: SystemuserTokenProvider,
                      private val configuration: Configuration) : HealthCheck {
    private val log = LoggerFactory.getLogger(PdlService::class.java)
    private val adressebeskyttelseQuery: String = lastQueryFraFil("hentAdressebeskyttelse")

    fun harStrengtFortroligAdresse(fnr: String) = harKode6(fnr)
    fun harFortroligAdresse(fnr: String) = harKode7(fnr)
    fun harKode6(fnr: String): Boolean = harGradering(fnr, PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG)
    fun harKode7(fnr: String): Boolean = harGradering(fnr, PdlAdressebeskyttelseGradering.FORTROLIG)

    fun hentAdresseBeskyttelse(fnr: String): PdlAdressebeskyttelseGradering? {
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

    /* fun getHelsesjekk(): Pingable {
         val metadata = Pingable.Ping.PingMetadata(
                 "pdl",
                 configuration.PDL_API_URL,
                 "Henter adressebeskyttelse",
                 false
         )

         return Pingable {
             try {
                 val response = pdlClient.target(configuration.PDL_API_URL)
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
 */
    private fun harGradering(fnr: String, gradering: PdlAdressebeskyttelseGradering): Boolean {
        val pdlGradering = hentAdresseBeskyttelse(fnr)
        return gradering == pdlGradering
    }

    private fun graphqlRequest(request: PdlRequest): PdlResponse {
        val uuid = UUID.randomUUID()
        try {
            val ssotoken = SubjectHandler.getSsoToken().map { it.token }
            val oidctoken = SubjectHandler.getSsoToken(SsoToken.Type.OIDC)
            val veilederOidcToken: String = listOf(oidctoken, ssotoken)
                    .first { it.isPresent }
                    .orElseThrow { IllegalStateException("Kunne ikke hente ut bruker ssoToken") }

            log.info("Token extraction, found in ${ssotoken.isPresent} ${oidctoken.isPresent}")

            val consumerOidcToken: String = stsService.getSystemUserAccessToken()
                    ?: throw IllegalStateException("Kunne ikke hente ut systemusertoken")

            log.info("""
                    PDL-request: $uuid
                    ------------------------------------------------------------------------------------
                        callId: ${MDC.get(MDCConstants.MDC_CALL_ID)}
                        userTokenParts: ${veilederOidcToken.split(".").size}
                        consumerTokenParts: ${consumerOidcToken.split(".").size}
                    ------------------------------------------------------------------------------------
                """.trimIndent())

            val request: Request = Request.Builder()
                    .url(configuration.PDL_API_URL + "/graphql")
                    .addHeader("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID))
                    .addHeader("Nav-Consumer-Id", "mininnboks-api")
                    .addHeader("Authorization", "Bearer $veilederOidcToken")
                    .addHeader("Nav-Consumer-Token", "Bearer $consumerOidcToken")
                    .addHeader("Tema", "GEN")
                    .post(toJsonRequestBody(request))
                    .build()


            val response: Response = pdlClient.newCall(request).execute()
            val body = response.body()?.string()
            //val body = response.readEntity(String::class.java)
            log.info("""
                PDL-response: $uuid
                ------------------------------------------------------------------------------------
                    status: ${response.code()} ${response.message()}
                ------------------------------------------------------------------------------------
            """.trimIndent())

            val pdlResponse = body?.let { JacksonConfig.mapper.readValue<PdlResponse>(it) }

            if (pdlResponse?.errors?.isNotEmpty() == true) {
                val errorMessages = pdlResponse.errors.map { it.message }.joinToString(", ")
                log.info(
                        """
                    PDL-response: $uuid
                    ------------------------------------------------------------------------------------
                        status: ${response.code()} ${response.message()}
                        errors: $errorMessages
                    ------------------------------------------------------------------------------------
                """.trimIndent()
                )
                throw Exception(errorMessages)
            }

            return pdlResponse!!
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

    override fun checkHealth(): HealthCheckResult {
        TODO("Not yet implemented")
    }
}


private fun lastQueryFraFil(name: String): String {
    return PdlService::class.java
            .getResource("/pdl/$name.graphql")
            .readText()
            .replace("[\n\r]", "")
}
