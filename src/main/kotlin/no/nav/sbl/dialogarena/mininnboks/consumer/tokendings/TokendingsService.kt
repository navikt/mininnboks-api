package no.nav.sbl.dialogarena.mininnboks.consumer.tokendings

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.time.Instant
import java.util.*

interface TokendingsService {
    suspend fun exchangeToken(token: String, targetApp: String): String
}

class TokendingsServiceImpl(
    private val tokendingsConsumer: TokendingsConsumer,
    private val jwtAudience: String,
    private val clientId: String,
    privateJwk: String
) : TokendingsService {
    private val privateRsaKey = RSAKey.parse(privateJwk)

    override suspend fun exchangeToken(token: String, targetApp: String): String {
        val jwt = createSignedAssertion(clientId, jwtAudience, privateRsaKey)

        return tokendingsConsumer.exchangeToken(token, jwt, targetApp).accessToken
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
