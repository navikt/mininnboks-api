package no.nav.sbl.dialogarena.mininnboks.consumer

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JavaType
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import no.nav.common.auth.subject.Subject
import no.nav.common.log.MDCConstants
import no.nav.common.utils.IdUtils
import no.nav.sbl.dialogarena.mininnboks.JacksonUtils
import no.nav.sbl.dialogarena.mininnboks.common.TjenestekallLogger
import no.nav.sbl.dialogarena.mininnboks.externalCall
import org.slf4j.MDC

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class GraphQLError(
    val message: String,
    val locations: List<SourceLocation>? = null,
    val path: List<Any>? = null,
    val extensions: Map<String, Any?>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class SourceLocation(
    val line: Int,
    val column: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class GraphQLResponse<DATA>(
    val data: DATA? = null,
    val errors: List<GraphQLError>? = null,
    val extensions: Map<String, Any?>? = null
)

interface GraphQLVariables
interface GraphQLResult

interface GraphQLRequest<VARIABLES : GraphQLVariables, RETURN_TYPE : GraphQLResult> {
    val query: String
    val variables: VARIABLES

    @get:JsonIgnore
    val expectedReturnType: Class<RETURN_TYPE>
}

data class GraphQLClientConfig(
    val tjenesteNavn: String,
    val requestConfig: suspend HttpRequestBuilder.(callId: String, subject: Subject) -> Unit
)

class GraphQLClient(
    private val config: GraphQLClientConfig,
    private val httpClient: HttpClient
) {
    suspend fun <VARS : GraphQLVariables, DATA : GraphQLResult, REQUEST : GraphQLRequest<VARS, DATA>> execute(
        subject: Subject,
        request: REQUEST
    ): GraphQLResponse<DATA> = externalCall(subject) {
        val callId = MDC.get(MDCConstants.MDC_CALL_ID) ?: IdUtils.generateId()
        val requestId = IdUtils.generateId()
        try {
            TjenestekallLogger.info(
                "${config.tjenesteNavn}-request: $callId ($requestId)",
                mapOf(
                    "subject" to subject.uid,
                    "request" to request
                )
            )

            val httpResponse: HttpResponse = httpClient.request {
                method = HttpMethod.Post
                contentType(ContentType.Application.Json)
                config.requestConfig.invoke(this, callId, subject)
                body = request
            }

            val body: String = httpResponse.receive()
            val typeReference: JavaType = JacksonUtils.objectMapper.typeFactory
                .constructParametricType(GraphQLResponse::class.java, request.expectedReturnType)
            val response = JacksonUtils.objectMapper.readValue<GraphQLResponse<DATA>>(body, typeReference)

            val tjenestekallFelter = mapOf(
                "status" to httpResponse.status,
                "data" to response.data,
                "errors" to response.errors
            )

            if (response.errors?.isNotEmpty() == true) {
                TjenestekallLogger.error("${config.tjenesteNavn}-response-error: $callId ($requestId)", tjenestekallFelter)
                val errorMessages = response.errors.joinToString(", ") { it.message }
                throw Exception(errorMessages)
            }
            TjenestekallLogger.info("${config.tjenesteNavn}-response: $callId ($requestId)", tjenestekallFelter)

            return@externalCall response
        } catch (exception: Exception) {
            TjenestekallLogger.error("${config.tjenesteNavn}-response-error: $callId ($requestId)", mapOf("exception" to exception))
            throw exception
        }
    }

    companion object {
        fun lastQueryFraFil(source: String, name: String): String {
            return GraphQLClient::class.java
                .getResource("/$source/$name.graphql")
                .readText()
                .replace("[\n\r]", "")
        }
    }
}
