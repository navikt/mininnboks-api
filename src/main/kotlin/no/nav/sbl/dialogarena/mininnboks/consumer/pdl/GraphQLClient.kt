package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JavaType
import no.nav.common.auth.subject.Subject
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestUtils
import no.nav.sbl.dialogarena.mininnboks.JacksonUtils
import no.nav.sbl.dialogarena.mininnboks.externalCall
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
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
    val requestConfig: Request.Builder.(callId: String, subject: Subject) -> Unit
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
            val httpRequest: Request = createRequest(
                callId = callId,
                subject = subject,
                request = request
            )
            val httpResponse: Response = httpClient.newCall(httpRequest).execute()
            val body = httpResponse.body()?.string()
            log.info(
                """
                    ${config.tjenesteNavn}-response: $callId
                    ------------------------------------------------------------------------------------
                        status: ${httpResponse.code()} ${httpResponse.message()}
                    ------------------------------------------------------------------------------------
                """.trimIndent()
            )
            requireNotNull(body) {
                "${config.tjenesteNavn}-Response-body was empty"
            }

            val typeReference: JavaType = JacksonUtils.objectMapper.typeFactory
                .constructParametricType(GraphQLResponse::class.java, request.expectedReturnType)

            val response: GraphQLResponse<DATA> = body.let { JacksonUtils.objectMapper.readValue(it, typeReference) }
            if (response.errors?.isNotEmpty() == true) {
                val errorMessages = response.errors.joinToString(", ") { it.message }
                log.info(
                    """
                        ${config.tjenesteNavn}-response: $callId
                        ------------------------------------------------------------------------------------
                            status: ${httpResponse.code()} ${httpResponse.message()}
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

    private fun <VARS : GraphQLVariables, DATA : GraphQLResult, REQUEST : GraphQLRequest<VARS, DATA>> createRequest(
        callId: String,
        subject: Subject,
        request: REQUEST
    ): Request {
        return Request.Builder()
            .apply {
                config.requestConfig(this, callId, subject)
            }
            .post(RestUtils.toJsonRequestBody(request))
            .build()
    }
}
