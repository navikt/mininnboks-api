package no.nav.sbl.dialogarena.mininnboks.consumer

import no.finn.unleash.DefaultUnleash
import no.finn.unleash.Unleash
import no.finn.unleash.UnleashContext
import no.finn.unleash.event.UnleashSubscriber
import no.finn.unleash.repository.FeatureToggleResponse
import no.finn.unleash.strategy.Strategy
import no.finn.unleash.util.UnleashConfig
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.SubjectHandler
import no.nav.common.health.HealthCheck
import no.nav.common.health.HealthCheckResult
import no.nav.common.utils.EnvironmentUtils
import no.nav.sbl.dialogarena.mininnboks.consumer.UnleashService.FeatureToggle

interface UnleashService : HealthCheck {
    class FeatureToggle(val name: String)
    object Toggles {
        val stengSTO = FeatureToggle("modia.innboks.steng-sto")
        val brukerSalesforce = FeatureToggle("modia.innboks.bruker-salesforce-dialoger")
        val oksosAdressesok = FeatureToggle("modia.innboks.oksos-adressesok")
    }
    fun isEnabled(toggleName: FeatureToggle): Boolean
    fun isEnabled(toggleName: FeatureToggle, context: UnleashContext): Boolean
}

class UnleashServiceImpl : UnleashService, UnleashSubscriber {
    companion object {
        fun resolveUnleashContext(): UnleashContext {
            val subject: String? = SubjectHandler.getIdent().orElse(null)
            val token: String? = SubjectHandler.getSsoToken().map(SsoToken::getToken).orElse(null)
            return UnleashContext.builder()
                .userId(subject)
                .sessionId(token)
                .build()
        }
    }
    private val unleash: Unleash
    var lastFetchStatus: FeatureToggleResponse.Status? = null

    constructor(unleash: Unleash) {
        this.unleash = unleash
    }

    constructor(vararg strategies: Strategy) {
        val config = UnleashConfig.builder()
            .appName("mininnboks-api")
            .unleashAPI(EnvironmentUtils.getRequiredProperty("UNLEASH_API_URL"))
            .subscriber(this)
            .build()
        this.unleash = DefaultUnleash(config, *strategies)
    }

    override fun isEnabled(toggleName: FeatureToggle): Boolean = unleash.isEnabled(toggleName.name, resolveUnleashContext())
    override fun isEnabled(toggleName: FeatureToggle, context: UnleashContext): Boolean = unleash.isEnabled(toggleName.name, context)

    override fun togglesFetched(toggleResponse: FeatureToggleResponse?) {
        this.lastFetchStatus = toggleResponse?.status
    }

    override fun checkHealth(): HealthCheckResult {
        return when (lastFetchStatus) {
            FeatureToggleResponse.Status.CHANGED -> HealthCheckResult.healthy()
            FeatureToggleResponse.Status.NOT_CHANGED -> HealthCheckResult.healthy()
            FeatureToggleResponse.Status.UNAVAILABLE -> HealthCheckResult.unhealthy(lastFetchStatus?.toString())
            else -> HealthCheckResult.unhealthy("No status found")
        }
    }
}

class ByEnvironmentStrategy : Strategy {
    val ENVIRONMENT_PROPERTY = "APP_ENVIRONMENT"
    override fun getName() = "byEnvironment"

    override fun isEnabled(parameters: MutableMap<String, String>?): Boolean {
        val miljo = (parameters?.get("miljø") ?: "").split(",")
        return miljo.any(::isCurrentEnvironment)
    }

    private fun isCurrentEnvironment(env: String): Boolean {
        return EnvironmentUtils.getOptionalProperty(ENVIRONMENT_PROPERTY).orElse("local") == env
    }
}
