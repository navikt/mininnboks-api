package no.nav.sbl.dialogarena.mininnboks.common

import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.utils.EnvironmentUtils
import no.nav.common.utils.SslUtils
import java.util.*

class TruststoreCheck : HealthCheck {
    override fun checkHealth(): HealthCheckResult {
        val truststore: Optional<String> = EnvironmentUtils
            .getOptionalProperty(SslUtils.NAV_TRUSTSTORE_PATH)
        return truststore
            .map { HealthCheckResult.healthy() }
            .orElseGet { HealthCheckResult.unhealthy(truststore.orElse("N/A")) }
    }

    companion object {
        private val INSTANCE = TruststoreCheck()
        fun asSelftestCheck(): SelfTestCheck {
            return SelfTestCheck(
                "Sjekker at truststore er satt",
                true,
                INSTANCE
            )
        }
    }
}
