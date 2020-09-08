package no.nav.sbl.dialogarena.mininnboks

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.Payload
import io.ktor.application.ApplicationCall
import io.ktor.auth.Principal
import io.ktor.auth.jwt.JWTCredential
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.http.auth.HttpAuthHeader
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit


private val logger = LoggerFactory.getLogger("mininnboks-api.JwtConfig")

class JwtUtil {
    companion object {
        fun useJwtFromCookie(call: ApplicationCall): HttpAuthHeader? {
            return try {
                val token = call.request.cookies["?"]
                io.ktor.http.auth.parseAuthorizationHeader("Bearer $token")
            } catch (ex: Throwable) {
                logger.error("Illegal HTTP auth header", ex)
                null
            }
        }

        fun makeJwkProvider(jwksUrl: String): JwkProvider =
                JwkProviderBuilder(URL(jwksUrl))
                        .cached(10, 24, TimeUnit.HOURS)
                        .rateLimited(10, 1, TimeUnit.MINUTES)
                        .build()

        fun validateJWT(credentials: JWTCredential): Principal? {
            return try {
                requireNotNull(credentials.payload.audience) { "Audience not present" }
                JWTPrincipal(credentials.payload)
            } catch (e: Exception) {
                logger.error("Failed to validate JWT token", e)
                null
            }
        }
    }
}


class MockPayload(val staticSubject: String) : Payload {
    override fun getSubject(): String {
        return staticSubject
    }

    override fun getExpiresAt(): Date {
        TODO("not implemented")
    }

    override fun getIssuer(): String {
        TODO("not implemented")
    }

    override fun getAudience(): MutableList<String> {
        TODO("not implemented")
    }

    override fun getId(): String {
        TODO("not implemented")
    }

    override fun getClaims(): MutableMap<String, Claim> {
        TODO("not implemented")
    }

    override fun getIssuedAt(): Date {
        TODO("not implemented")
    }

    override fun getClaim(name: String?): Claim {
        TODO("not implemented")
    }

    override fun getNotBefore(): Date {
        TODO("not implemented")
    }
}
