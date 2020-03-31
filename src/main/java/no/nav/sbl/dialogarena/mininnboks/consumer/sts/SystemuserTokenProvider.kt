package no.nav.sbl.dialogarena.mininnboks.consumer.sts

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.nimbusds.jwt.JWT
import com.nimbusds.jwt.JWTParser
import lombok.SneakyThrows
import no.nav.sbl.dialogarena.mininnboks.config.utils.JacksonConfig
import no.nav.sbl.rest.RestUtils
import java.text.ParseException
import java.util.*
import javax.ws.rs.client.Client
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType

const val MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH = 60 * 1000L

interface SystemuserTokenProvider {
    companion object {
        @JvmStatic
        fun fromDiscoveryUrl(
                discoveryUrl: String,
                srvUsername: String,
                srvPassword: String,
                client: Client = RestUtils.createClient()
        ): SystemuserTokenProvider = SystemuserTokenProviderImpl(true, discoveryUrl, srvUsername, srvPassword, client)

        @JvmStatic
        fun fromTokenEndpoint(
                tokenEndpointUrl: String,
                srvUsername: String,
                srvPassword: String,
                client: Client = RestUtils.createClient()
        ): SystemuserTokenProvider = SystemuserTokenProviderImpl(false, tokenEndpointUrl, srvUsername, srvPassword, client)
    }

    fun getSystemUserAccessToken(): String?
}
// TODO Kan erstattes av tilsvarende klass i common etter bump.
class SystemuserTokenProviderImpl internal constructor(
        startingUrlIsDiscoveryUrl: Boolean,
        startingUrl: String,
        private val srvUsername: String,
        private val srvPassword: String,
        private val client: Client
) : SystemuserTokenProvider {
    private var accessToken: JWT? = null
    private val tokenEndpointUrl: String = if (startingUrlIsDiscoveryUrl) {
        hentOidcDiscoveryConfiguration(client, startingUrl).tokenEndpoint
    } else {
        startingUrl
    }

    override fun getSystemUserAccessToken(): String? {
        if (tokenIsSoonExpired()) {
            refreshToken()
        }
        return accessToken!!.parsedString
    }

    @SneakyThrows(ParseException::class)
    private fun refreshToken() {
        val clientCredentials = fetchSystemUserAccessToken()
        this.accessToken = JWTParser.parse(clientCredentials!!.accessToken)
    }

    private fun tokenIsSoonExpired(): Boolean {
        return accessToken == null || expiresWithin(accessToken!!, MINIMUM_TIME_TO_EXPIRE_BEFORE_REFRESH)
    }

    private fun fetchSystemUserAccessToken(): ClientCredentialsResponse? {
        val targetUrl = "$tokenEndpointUrl?grant_type=client_credentials&scope=openid"
        val basicAuth: String = basicCredentials(srvUsername, srvPassword)
        val response = client
                .target(targetUrl)
                .request()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, basicAuth)
                .get()

        val strResponse = response.readEntity(String::class.java)
        if (response.status != 200) {
            throw RuntimeException(String.format("Received unexpected status %d when requesting access token for system user. Response: %s", response.status, strResponse))
        }
        return JacksonConfig.mapper.readValue(strResponse, ClientCredentialsResponse::class.java)
    }
}

private fun basicCredentials(username: String, password: String): String {
    return "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", username, password).toByteArray())
}

private fun expiresWithin(jwt: JWT, withinMillis: Long): Boolean {
    return try {
        val tokenExpiration: Date = jwt.getJWTClaimsSet().getExpirationTime()
        val expirationTime = tokenExpiration.time - withinMillis
        System.currentTimeMillis() > expirationTime
    } catch (e: ParseException) {
        true
    }
}

private fun hentOidcDiscoveryConfiguration(client: Client, discoveryUrl: String): OidcDiscoveryConfiguration {
    return client
            .target(discoveryUrl)
            .request()
            .get()
            .readEntity(OidcDiscoveryConfiguration::class.java)
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
