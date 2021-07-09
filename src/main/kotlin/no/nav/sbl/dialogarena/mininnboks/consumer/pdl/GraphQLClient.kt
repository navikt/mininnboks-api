package no.nav.sbl.dialogarena.mininnboks.consumer.pdl

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.common.auth.subject.Subject
import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.JacksonUtils
import no.nav.sbl.dialogarena.mininnboks.externalCall
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
    val requestConfig: HttpRequestBuilder.(callId: String, subject: Subject) -> Unit
)

class GraphQLClient(
    private val httpClient: HttpClient,
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

            val block: HttpRequestBuilder.() -> Unit = {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                config.requestConfig.invoke(this, callId, subject)
                body = request
            }

            val httpResponse: HttpResponse = httpClient.request(block)

            val body: String = httpResponse.receive()

            val httpResponseMessage = JacksonUtils.objectMapper.readValue<String>(body)

            log.info(
                """
                    ${config.tjenesteNavn}-response: $callId
                    ------------------------------------------------------------------------------------
                        status: ${httpResponse.status} $httpResponseMessage
                    ------------------------------------------------------------------------------------
                """.trimIndent()
            )

            val typeReference: JavaType = JacksonUtils.objectMapper.typeFactory
                .constructParametricType(GraphQLResponse::class.java, request.expectedReturnType)
            val response = JacksonUtils.objectMapper.readValue<GraphQLResponse<DATA>>(body, typeReference)

            if (response.errors?.isNotEmpty() == true) {
                val errorMessages = response.errors.joinToString(", ") { it.message }
                log.info(
                    """
                        ${config.tjenesteNavn}-response: $callId
                        ------------------------------------------------------------------------------------
                            status: ${httpResponse.status} ${response.data}
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
