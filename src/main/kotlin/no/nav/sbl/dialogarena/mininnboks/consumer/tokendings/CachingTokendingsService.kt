package no.nav.sbl.dialogarena.mininnboks.consumer.tokendings

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import io.ktor.client.*
import kotlinx.coroutines.runBlocking
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.JwtUtil
import no.nav.sbl.dialogarena.mininnboks.consumer.tokendings.CachingTokendingsServiceImpl.TokendingsCacheKey
import no.nav.sbl.dialogarena.mininnboks.consumer.tokendings.CachingTokendingsServiceImpl.TokendingsCacheValue
import java.util.concurrent.TimeUnit

class CachingTokendingsServiceImpl(
    httpClient: HttpClient,
    configuration: Configuration
) : TokendingsService {
    data class TokendingsCacheKey(
        val subject: String,
        val targetApp: String
    )
    data class TokendingsCacheValue(
        val accessToken: String,
        val expiresInSeconds: Long
    )

    private val tokendingsService = TokendingsServiceImpl(httpClient, configuration)
    private val cache: Cache<TokendingsCacheKey, TokendingsCacheValue> = Caffeine
        .newBuilder()
        .expireAfter(ExpirationPolicy(expiryMarginInSeconds = 10))
        .maximumSize(1000)
        .build()

    override suspend fun exchangeToken(token: String, targetApp: String): String {
        val subject = JwtUtil.extractSubject(token)
        val cacheKey = TokendingsCacheKey(subject, targetApp)
        return cache.get(cacheKey) {
            runBlocking {
                val response = tokendingsService.exchangeTokenForResponse(token, targetApp)
                TokendingsCacheValue(response.accessToken, response.expiresIn.toLong())
            }
        }.accessToken
    }

    override val selftestCheck: SelfTestCheck = tokendingsService.selftestCheck
}

class ExpirationPolicy(val expiryMarginInSeconds: Long) : Expiry<TokendingsCacheKey, TokendingsCacheValue> {

    override fun expireAfterCreate(key: TokendingsCacheKey, value: TokendingsCacheValue, currentTime: Long): Long {
        return TimeUnit.SECONDS.toNanos(value.expiresInSeconds - expiryMarginInSeconds)
    }

    override fun expireAfterUpdate(
        key: TokendingsCacheKey?,
        value: TokendingsCacheValue?,
        currentTime: Long,
        currentDuration: Long
    ): Long = currentDuration

    override fun expireAfterRead(
        key: TokendingsCacheKey?,
        value: TokendingsCacheValue?,
        currentTime: Long,
        currentDuration: Long
    ): Long = currentDuration
}
