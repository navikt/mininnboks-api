package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.brukerdialog.security.oidc.SystemUserTokenProvider
import no.nav.common.auth.SsoToken
import no.nav.common.auth.SubjectHandler
import no.nav.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.config.ServiceConfig
import no.nav.sbl.dialogarena.mininnboks.config.utils.JacksonConfig
import no.nav.sbl.util.EnvironmentUtils.getRequiredProperty
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*
import javax.ws.rs.client.Client
import javax.ws.rs.client.Entity

class PdlService(val pdlClient: Client, val stsService: SystemUserTokenProvider) {
    private val log = LoggerFactory.getLogger(PdlService::class.java)
    private val adressebeskyttelseQuery: String = lastQueryFraFil("hentAdressebeskyttelse")

    fun harAdressebeskyttelse(fnr: String): Boolean {
        val response: PdlResponse? = graphqlRequest(PdlRequest(adressebeskyttelseQuery, Variables(fnr)))
        val adressebeskyttelse: List<PdlAdressebeskyttelse> = response
                ?.data
                ?.hentPerson
                ?.adressebeskyttelse
                ?: emptyList()

        return adressebeskyttelse
                .map { it.gradering }
                .any { it != PdlAdressebeskyttelseGradering.UGRADERT }
    }

    private fun graphqlRequest(request: PdlRequest): PdlResponse? {
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
                log.info("""
                PDL-response: $uuid
                ------------------------------------------------------------------------------------
                    status: ${response.status} ${response.statusInfo}
                    errors: ${pdlResponse.errors.map { it.message }.joinToString(", ")}
                ------------------------------------------------------------------------------------
            """.trimIndent())
                return null
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
            return null
        }
    }
}


private fun lastQueryFraFil(name: String): String {
    return PdlService::class.java
            .getResource("pdl/$name.graphql")
            .readText()
            .replace("[\n\r]", "")
}
