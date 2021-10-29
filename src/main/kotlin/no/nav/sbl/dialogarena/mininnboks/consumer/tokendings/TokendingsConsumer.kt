package no.nav.sbl.dialogarena.mininnboks.consumer.tokendings

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@JsonIgnoreProperties(ignoreUnknown = true)
data class TokendingsTokenResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("issued_token_type") val issuedTokenType: String,
    @JsonProperty("token_type") val tokenType: String,
    @JsonProperty("expires_in") val expiresIn: Int
)
class TokendingsConsumer(
    private val httpClient: HttpClient,
    private val metadata: TokendingsConfigurationMetadata
) {
    suspend fun exchangeToken(subjectToken: String, clientAssertion: String, audience: String): TokendingsTokenResponse {
        return withContext(Dispatchers.IO) {
            val urlParameters = ParametersBuilder().apply {
                append("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange")
                append("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer")
                append("client_assertion", clientAssertion)
                append("subject_token_type", "urn:ietf:params:oauth:token-type:jwt")
                append("subject_token", subjectToken)
                append("audience", audience)
            }.build()

            httpClient.post<TokendingsTokenResponse> {
                url(metadata.tokenEndpoint)
                body = TextContent(urlParameters.formUrlEncode(), ContentType.Application.FormUrlEncoded)
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class TokendingsConfigurationMetadata(
        @JsonProperty("issuer") val issuer: String,
        @JsonProperty("token_endpoint") val tokenEndpoint: String,
        @JsonProperty("jwks_uri") val jwksUri: String
    )

    companion object {
        fun fetchMetadata(httpClient: HttpClient, wellKnownUrl: String): TokendingsConfigurationMetadata = runBlocking {
            withContext(Dispatchers.IO) {
                httpClient.request<TokendingsConfigurationMetadata> {
                    method = HttpMethod.Get
                    url(wellKnownUrl)
                    accept(ContentType.Application.Json)
                }
            }
        }
    }
}
