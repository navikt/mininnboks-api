package no.nav.sbl.dialogarena.mininnboks.consumer.sts

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTParser
import io.ktor.client.request.*
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.oidc.discovery.OidcDiscoveryConfiguration
import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.ServiceConfig
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import java.text.ParseException
import java.util.*
import javax.ws.rs.core.HttpHeaders

const val MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH = 60 * 1000L

interface SystemuserTokenProvider {
    companion object {
        fun fromDiscoveryUrl(
            discoveryUrl: String,
            srvUsername: String,
            srvPassword: String,
            stsApiKey: String
        ): SystemuserTokenProvider = SystemuserTokenProviderImpl(true, discoveryUrl, srvUsername, srvPassword, stsApiKey)

        fun fromTokenEndpoint(
            tokenEndpointUrl: String,
            srvUsername: String,
            srvPassword: String,
            stsApiKey: String
        ): SystemuserTokenProvider = SystemuserTokenProviderImpl(false, tokenEndpointUrl, srvUsername, srvPassword, stsApiKey)
    }

    suspend fun getSystemUserAccessToken(): String?
}

// TODO Kan erstattes av tilsvarende klass i common etter bump.
class SystemuserTokenProviderImpl internal constructor(
    startingUrlIsDiscoveryUrl: Boolean,
    startingUrl: String,
    private val srvUsername: String,
    private val srvPassword: String,
    private val stsApiKey: String
) : SystemuserTokenProvider {

    private val log = LoggerFactory.getLogger(SystemuserTokenProvider::class.java)
    private var accessToken: JWT? = null
    private val tokenEndpointUrl: String? = if (startingUrlIsDiscoveryUrl) {
        runBlocking {
            hentOidcDiscoveryConfiguration(startingUrl, stsApiKey)?.tokenEndpoint
        }
    } else {
        startingUrl
    }

    override suspend fun getSystemUserAccessToken(): String? {
        if (tokenIsSoonExpired()) {
            refreshToken()
        }
        return accessToken?.parsedString
    }

    private suspend fun refreshToken() {
        val clientCredentials = fetchSystemUserAccessToken()
        this.accessToken = JWTParser.parse(clientCredentials?.accessToken)
    }

    private fun tokenIsSoonExpired(): Boolean {
        return accessToken == null || expiresWithin(accessToken!!, MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH)
    }

    private suspend fun fetchSystemUserAccessToken(): ClientCredentialsResponse {
        val targetUrl = "$tokenEndpointUrl?grant_type=client_credentials&scope=openid"
        val basicAuth: String = basicCredentials(srvUsername, srvPassword)

        return ServiceConfig.ktorClient.get(targetUrl) {
            header("Nav-Call-Id", MDC.get(MDCConstants.MDC_CALL_ID))
            header(HttpHeaders.AUTHORIZATION, basicAuth)
            header("x-nav-apiKey", stsApiKey)
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

    private suspend fun hentOidcDiscoveryConfiguration(discoveryUrl: String, stsApiKey: String): OidcDiscoveryConfiguration? {
        return ServiceConfig.ktorClient.get<OidcDiscoveryConfiguration>(discoveryUrl) {
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
