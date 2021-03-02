package no.nav.sbl.dialogarena.mininnboks.common

import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import java.io.File

class DiskCheck : HealthCheck {
    override fun checkHealth(): HealthCheckResult {
        val freeSpace = DISK.freeSpace
        return if (freeSpace > LIMIT) {
            HealthCheckResult.healthy()
        } else HealthCheckResult.unhealthy(String.format("Mindre enn %s MB ledig diskplass for %s", LIMIT / 1000000, DISK))
    }

    companion object {
        private val INSTANCE = DiskCheck()
        private const val LIMIT = 300000000L
        private val DISK = File(".").absoluteFile
        fun asSelftestCheck(): SelfTestCheck {
            return SelfTestCheck(
                String.format("Sjekk for om det er mindre enn %s MB diskplass ledig", LIMIT / 1000000),
                false,
                INSTANCE
            )
        }
    }
}
