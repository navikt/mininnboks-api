package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import com.fasterxml.jackson.annotation.JsonIgnore
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.subject.Subject
import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.externalCall
import no.nav.sbl.dialogarena.mininnboks.ktorClient
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.util.*

interface GraphQLVariables
interface GraphQLResult
data class GraphQLError(val message: String)
interface GraphQLRequest<VARIABLES : GraphQLVariables, RETURN_TYPE : GraphQLResult> {
    val query: String
    val variables: VARIABLES

    @get:JsonIgnore
    val expectedReturnType: Class<RETURN_TYPE>
}
data class GraphQLResponse<DATA>(
    val errors: List<GraphQLError>?,
    val data: DATA?
)

data class GraphQLClientConfig(
    val tjenesteNavn: String,
    val requestConfig: (callId: String, subject: Subject) -> HttpRequestBuilder
)

class GraphQLClient(
    private val config: GraphQLClientConfig
) {
    private val log = LoggerFactory.getLogger(GraphQLClient::class.java)

    suspend fun <VARS : GraphQLVariables, DATA : GraphQLResult, REQUEST : GraphQLRequest<VARS, DATA>> execute(
        subject: Subject,
        request: REQUEST
    ): GraphQLResponse<DATA> = externalCall(subject) {
        val callId = MDC.get(MDCConstants.MDC_CALL_ID) ?: UUID.randomUUID().toString()
        try {
            log.info(
                """
                    ${config.tjenesteNavn}-request: $callId
                    ------------------------------------------------------------------------------------
                        callId: ${MDC.get(MDCConstants.MDC_CALL_ID)}
                    ------------------------------------------------------------------------------------
                """.trimIndent()
            )

            // Hvorfor må denne kjøres i runBlocking? Vi kjører i en suspend-metode
            val response = runBlocking {
                ktorClient.post<GraphQLResponse<DATA>> {
                    body = request
                    apply {
                        config.requestConfig(callId, subject)
                    }
                }
            }

            if (response.errors?.isNotEmpty() == true) {
                val errorMessages = response.errors.joinToString(", ") { it.message }
                log.info(
                    """
                        ${config.tjenesteNavn}-response: $callId
                        ------------------------------------------------------------------------------------
                            errors: $errorMessages
                        ------------------------------------------------------------------------------------
                    """.trimIndent()
                )
                throw Exception(errorMessages)
            }

            return@externalCall response
        } catch (exception: Exception) {
            log.error(
                """
                    ${config.tjenesteNavn}-response: $callId
                    ------------------------------------------------------------------------------------
                        exception:
                        $exception
                    ------------------------------------------------------------------------------------
                """.trimIndent()
            )

            throw exception
        }
    }
}
