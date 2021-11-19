package no.nav.sbl.dialogarena.mininnboks.consumer.tokendings

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import io.ktor.client.*
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.createHttpClient
import java.time.Instant
import java.util.*

interface TokendingsService {
    suspend fun exchangeToken(token: String, targetApp: String): String
    val selftestCheck: SelfTestCheck
}

class TokendingsServiceImpl(
    private val configuration: Configuration,
    private val httpClient: HttpClient = createHttpClient("Tokendings")
) : TokendingsService {

    private val privateRsaKey = RSAKey.parse(configuration.TOKEN_X_PRIVATE_JWK)
    private val tokendingsMetadata: TokendingsConsumer.TokendingsConfigurationMetadata by lazy {
        TokendingsConsumer.fetchMetadata(
            httpClient,
            configuration.TOKEN_X_WELL_KNOWN_URL
        )
    }
    private val tokendingsConsumer: TokendingsConsumer by lazy {
        TokendingsConsumer(
            httpClient = httpClient,
            metadata = tokendingsMetadata
        )
    }

    suspend fun exchangeTokenForResponse(token: String, targetApp: String): TokendingsTokenResponse {
        val audience = tokendingsMetadata.tokenEndpoint
        val jwt = createSignedAssertion(configuration.TOKEN_X_CLIENT_ID, audience, privateRsaKey)

        return tokendingsConsumer.exchangeToken(token, jwt, targetApp)
    }

    override suspend fun exchangeToken(token: String, targetApp: String): String {
        return exchangeTokenForResponse(token, targetApp).accessToken
    }

    override val selftestCheck = SelfTestCheck(
        "Tokendings",
        true
    ) {
        runCatching {
            TokendingsConsumer.fetchMetadata(
                httpClient,
                configuration.TOKEN_X_WELL_KNOWN_URL
            )
        }.fold(
            onSuccess = { HealthCheckResult.healthy() },
            onFailure = { exception -> HealthCheckResult.unhealthy(exception) }
        )
    }

    private fun createSignedAssertion(clientId: String, audience: String, rsaKey: RSAKey): String {
        val now = Date.from(Instant.now())
        return JWTClaimsSet.Builder()
            .subject(clientId)
            .issuer(clientId)
            .audience(audience)
            .issueTime(now)
            .notBeforeTime(now)
            .expirationTime(Date.from(Instant.now().plusSeconds(60)))
            .jwtID(UUID.randomUUID().toString())
            .build()
            .sign(rsaKey)
            .serialize()
    }

    private fun JWTClaimsSet.sign(rsaKey: RSAKey): SignedJWT =
        SignedJWT(
            JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(rsaKey.keyID)
                .type(JOSEObjectType.JWT).build(),
            this
        ).apply {
            sign(RSASSASigner(rsaKey.toPrivateKey()))
        }
}
