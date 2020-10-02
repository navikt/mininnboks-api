package no.nav.sbl.dialogarena.mininnboks

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.http.auth.*
import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import org.slf4j.LoggerFactory
import java.net.URL
import java.util.concurrent.TimeUnit


private val logger = LoggerFactory.getLogger("mininnboks-api.JwtConfig")

class JwtUtil {
    companion object {
        fun useJwtFromCookie(call: ApplicationCall): HttpAuthHeader? {
            return try {
                val token = call.request.cookies["selvbetjening-idtoken"]?: call.request.headers["Authorization"]
                parseAuthorizationHeader("Bearer $token")
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

        fun validateJWT(call: ApplicationCall, credentials: JWTCredential): Principal? {
            return try {
                requireNotNull(credentials.payload.audience) { "Audience not present" }
                val token = call.request.cookies["selvbetjening-idtoken"]
                SubjectPrincipal(Subject(
                        credentials.payload.subject,
                        IdentType.EksternBruker,
                        SsoToken.oidcToken(token, credentials.payload.claims)
                ))
            } catch (e: Exception) {
                logger.error("Failed to validate JWT token", e)
                null
            }
        }
    }
}

class SubjectPrincipal(val subject: Subject) : Principal
