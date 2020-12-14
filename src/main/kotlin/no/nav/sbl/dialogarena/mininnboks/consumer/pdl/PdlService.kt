package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import no.nav.common.auth.subject.Subject
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestUtils.toJsonRequestBody
import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.ObjectMapperProvider
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*

class PdlException(cause: Exception) : RuntimeException("Kunne ikke utlede adressebeskyttelse", cause)

open class PdlService(private val pdlClient: OkHttpClient,
                      private val stsService: SystemuserTokenProvider,
                      private val configuration: Configuration) {
    private val log = LoggerFactory.getLogger(PdlService::class.java)
    private val adressebeskyttelseQuery: String = lastQueryFraFil("hentAdressebeskyttelse")

    val selfTestCheck: SelfTestCheck = SelfTestCheck("Henter adressebeskyttelse: " + configuration.PDL_API_URL + "/graphql", false) {
        checkHealth()
    }

    suspend fun harStrengtFortroligAdresse(subject: Subject) = harKode6(subject)
    suspend fun harFortroligAdresse(subject: Subject) = harKode7(subject)
    suspend fun harKode6(subject: Subject): Boolean = harGradering(subject, PdlAdressebeskyttelseGradering.STRENGT_FORTROLIG)
    suspend fun harKode7(subject: Subject): Boolean = harGradering(subject, PdlAdressebeskyttelseGradering.FORTROLIG)

    suspend fun hentAdresseBeskyttelse(subject: Subject): PdlAdressebeskyttelseGradering? {
        return withContext(MDCContext()) {
            try {
                val response: PdlResponse? = graphqlRequest(PdlRequest(adressebeskyttelseQuery, Variables(subject.uid)), subject)
                response
                        ?.data
                        ?.hentPerson
                        ?.adressebeskyttelse
                        ?.firstOrNull()
                        ?.gradering
            } catch (exception: Exception) {
                log.error("Kunne ikke utlede adressebeskyttelse", exception)
                throw PdlException(exception)
            }
        }
    }

    private suspend fun harGradering(subject: Subject, gradering: PdlAdressebeskyttelseGradering): Boolean {
        val pdlGradering = hentAdresseBeskyttelse(subject)
        return gradering == pdlGradering
    }

    private fun graphqlRequest(pdlRequest: PdlRequest, subject: Subject): PdlResponse? {
        val uuid = UUID.randomUUID()
        try {
            val ssotoken = subject.ssoToken.token
            val consumerOidcToken: String = stsService.getSystemUserAccessToken()
                    ?: throw IllegalStateException("Kunne ikke hente ut systemusertoken")

            log.info("""
                    PDL-request: $uuid
                    ------------------------------------------------------------------------------------
                        callId: ${MDC.get(MDCConstants.MDC_CALL_ID)}
                        userTokenParts: ${ssotoken.split(".").size}
                        consumerTokenParts: ${consumerOidcToken.split(".").size}
                    ------------------------------------------------------------------------------------
                """.trimIndent())

            val request: Request = createRequest(ssotoken, consumerOidcToken, pdlRequest)

            return handleResponse(request, uuid)
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

    private fun handleResponse(request: Request, uuid: UUID?): PdlResponse? {
        val response: Response = pdlClient.newCall(request).execute()
        val body = response.body()?.string()
        log.info("""
                    PDL-response: $uuid
                    ------------------------------------------------------------------------------------
                        status: ${response.code()} ${response.message()}
                    ------------------------------------------------------------------------------------
                """.trimIndent())

        val pdlResponse = body?.let { ObjectMapperProvider.objectMapper.readValue<PdlResponse>(it) }

        if (pdlResponse?.errors?.isNotEmpty() == true) {
            val errorMessages = pdlResponse.errors.joinToString(", ") { it.message }
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

        return pdlResponse
    }

    private fun createRequest(veilederOidcToken: String, consumerOidcToken: String, pdlRequest: PdlRequest): Request {
        return Request.Builder()
                .url(configuration.PDL_API_URL + "/graphql")
                .addHeader("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID))
                .addHeader("Nav-Consumer-Id", "mininnboks-api")
                .addHeader("Authorization", "Bearer $veilederOidcToken")
                .addHeader("Nav-Consumer-Token", "Bearer $consumerOidcToken")
                .addHeader("Tema", "GEN")
                .addHeader("x-nav-apiKey", configuration.PDL_API_APIKEY)
                .post(toJsonRequestBody(pdlRequest))
                .build()
    }

    fun checkHealth(): HealthCheckResult {

        kotlin.runCatching {
            pingGraphQL()

        }.onSuccess {
            return if (it == 200)
                HealthCheckResult.healthy()
            else
                HealthCheckResult.unhealthy("Statuskode: $it")

        }.onFailure {
            return HealthCheckResult.unhealthy(it.message)
        }

        return HealthCheckResult.unhealthy("Feil ved Helsesjekk")
    }

    private fun pingGraphQL(): Int {
        val request: Request = Request.Builder()
                .url(configuration.PDL_API_URL + "/graphql")
                .method("OPTIONS", null)
                .addHeader("x-nav-apiKey", configuration.PDL_API_APIKEY)
                .build()

        val response: Response = pdlClient.newCall(request).execute()

        return if (response.isSuccessful) {
            response.code()
        } else {
            response.body()?.close()
            response.code()
        }
    }
}

private fun lastQueryFraFil(name: String): String {
    return PdlService::class.java
            .getResource("/pdl/$name.graphql")
            .readText()
            .replace("[\n\r]", "")
}