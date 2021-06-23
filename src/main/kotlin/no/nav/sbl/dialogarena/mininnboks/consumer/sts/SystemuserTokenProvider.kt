package no.nav.sbl.dialogarena.mininnboks.consumer.sts

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.readValue
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTParser
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.oidc.discovery.OidcDiscoveryConfiguration
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.common.rest.client.RestUtils
import no.nav.sbl.dialogarena.mininnboks.JacksonUtils
import no.nav.sbl.dialogarena.mininnboks.ktorClient
import okhttp3.OkHttpClient
import okhttp3.Request
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.text.ParseException
import java.util.*
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType

const val MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH = 60 * 1000L

interface SystemuserTokenProvider {
    companion object {
        fun fromDiscoveryUrl(
            discoveryUrl: String,
            srvUsername: String,
            srvPassword: String,
            stsApiKey: String,
            client: OkHttpClient = RestClient.baseClientBuilder().build()
        ): SystemuserTokenProvider = SystemuserTokenProviderImpl(true, discoveryUrl, srvUsername, srvPassword, stsApiKey, client)

        fun fromTokenEndpoint(
            tokenEndpointUrl: String,
            srvUsername: String,
            srvPassword: String,
            stsApiKey: String,
            client: OkHttpClient = RestClient.baseClientBuilder().build()
        ): SystemuserTokenProvider = SystemuserTokenProviderImpl(false, tokenEndpointUrl, srvUsername, srvPassword, stsApiKey, client)
    }

    fun getSystemUserAccessToken(): String?
}

// TODO Kan erstattes av tilsvarende klass i common etter bump.
class SystemuserTokenProviderImpl internal constructor(
    startingUrlIsDiscoveryUrl: Boolean,
    startingUrl: String,
    private val srvUsername: String,
    private val srvPassword: String,
    private val stsApiKey: String,
    private val client: OkHttpClient
) : SystemuserTokenProvider {

    private val log = LoggerFactory.getLogger(SystemuserTokenProvider::class.java)

    private var accessToken: JWT? = null
    private val tokenEndpointUrl: String? = if (startingUrlIsDiscoveryUrl) {
        hentOidcDiscoveryConfiguration(startingUrl, stsApiKey)?.tokenEndpoint
    } else {
        startingUrl
    }

    override fun getSystemUserAccessToken(): String? {
        if (tokenIsSoonExpired()) {
            refreshToken()
        }
        return accessToken?.parsedString
    }

    private fun refreshToken() {
        val clientCredentials = fetchSystemUserAccessToken()
        this.accessToken = JWTParser.parse(clientCredentials?.accessToken)
    }

    private fun tokenIsSoonExpired(): Boolean {
        return accessToken == null || expiresWithin(accessToken!!, MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH)
    }

    private fun fetchSystemUserAccessToken(): ClientCredentialsResponse? {
        val targetUrl = "$tokenEndpointUrl?grant_type=client_credentials&scope=openid"
        val basicAuth: String = basicCredentials(srvUsername, srvPassword)

        val request: Request = Request.Builder()
            .url(targetUrl)
            .addHeader("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
            .header(HttpHeaders.AUTHORIZATION, basicAuth)
            .header("x-nav-apiKey", stsApiKey)
            .get()
            .build()

        val response = client.newCall(request).execute()

        if (response.code() != 200) {
            val strResponse = RestUtils.parseJsonResponse(response, String::class.java)
            val errorMessage = String.format("Received unexpected status %d when requesting access token for system user. Response: %s", response.code(), strResponse)
            log.error("Failed to get SystemUserToken: $errorMessage")
            throw RuntimeException(errorMessage)
        } else {
            val body = response.body()?.string()
            val credentials = body?.let { JacksonUtils.objectMapper.readValue<ClientCredentialsResponse>(it) }
            log.info("Fetched SystemUserToken, parts: ${credentials?.accessToken?.split(".")?.size}")
            return credentials
        }
    }
}

private fun basicCredentials(username: String, password: String): String {
    return "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).toByteArray())
}

private fun expiresWithin(jwt: JWT, withinMillis: Long): Boolean {
    return try {
        val tokenExpiration: Date = jwt.jwtClaimsSet.expirationTime
        val expirationTime = tokenExpiration.time - withinMillis
        System.currentTimeMillis() > expirationTime
    } catch (e: ParseException) {
        true
    }
}

private fun hentOidcDiscoveryConfiguration(discoveryUrl: String, stsApiKey: String): OidcDiscoveryConfiguration? {
    // Denne må kjøre runBlocking siden den kjører i init av klassen?
    return runBlocking {
        ktorClient.get<OidcDiscoveryConfiguration>(discoveryUrl) {
            header("x-nav-apiKey", stsApiKey)
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
private data class ClientCredentialsResponse(
    @JsonProperty("access_token") val accessToken: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
private data class OidcDiscoveryConfiguration(
    @JsonProperty("jwks_uri") val jwksUri: String,
    @JsonProperty("token_endpoint") val tokenEndpoint: String,
    @JsonProperty("authorization_endpoint") val authorizationEndpoint: String,
    @JsonProperty("issuer") val issuer: String
)
