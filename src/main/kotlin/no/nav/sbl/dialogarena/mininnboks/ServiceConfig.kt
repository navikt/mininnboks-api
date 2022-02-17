package no.nav.sbl.dialogarena.mininnboks

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import no.nav.common.cxf.StsConfig
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.log.MDCConstants
import no.nav.sbl.dialogarena.mininnboks.PortUtils.portBuilder
import no.nav.sbl.dialogarena.mininnboks.PortUtils.portTypeSelfTestCheck
import no.nav.sbl.dialogarena.mininnboks.common.DiskCheck
import no.nav.sbl.dialogarena.mininnboks.common.TruststoreCheck
import no.nav.sbl.dialogarena.mininnboks.consumer.*
import no.nav.sbl.dialogarena.mininnboks.consumer.saf.SafService
import no.nav.sbl.dialogarena.mininnboks.consumer.saf.SafServiceImpl
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider.Companion.fromTokenEndpoint
import no.nav.sbl.dialogarena.mininnboks.consumer.tokendings.CachingTokendingsServiceImpl
import no.nav.sbl.dialogarena.mininnboks.consumer.tokendings.TokendingsService
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType
import org.slf4j.MDC
import java.util.*

class ServiceConfig(val configuration: Configuration) {
    val tokendingsService: TokendingsService = CachingTokendingsServiceImpl(configuration)
    val unleashService: UnleashService = UnleashServiceImpl(
        ByEnvironmentStrategy()
    )

    val henvendelsePortType = portBuilder(
        HenvendelsePortType::class.java,
        configuration.HENVENDELSE_WS_URL,
        "classpath:wsdl/Henvendelse.wsdl",
        stsConfig()
    )

    val innsynHenvendelsePortType = portBuilder(
        InnsynHenvendelsePortType::class.java,
        configuration.INNSYN_HENVENDELSE_WS_URL,
        "classpath:wsdl/InnsynHenvendelse.wsdl",
        stsConfig()
    )

    val henvendelseService = henvendelseService()
    val stsService = systemUserTokenProvider()
    val safService: SafService = SafServiceImpl(
        tokendings = tokendingsService,
        configuration = configuration
    )

    val selfTestCheckStsService: SelfTestCheck =
        SelfTestCheck("Sjekker at systembruker kan hente token fra STS", true) {
            runBlocking {
                MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
                withContext(MDCContext()) {
                    checkHealthStsService()
                }
            }
        }

    val selfTestCheckHenvendelse: SelfTestCheck = portTypeSelfTestCheck(
        "no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType"
    ) {
        henvendelsePortType.pingPort.ping()
    }

    val selfTestCheckInnsynHenvendelsePortType: SelfTestCheck = portTypeSelfTestCheck(
        "no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType"
    ) {
        innsynHenvendelsePortType.pingPort.ping()
    }

    val selfTestChecklist = listOf(
        tokendingsService.selftestCheck,
        selfTestCheckStsService,
        selfTestCheckHenvendelse,
        selfTestCheckInnsynHenvendelsePortType,
        DiskCheck.asSelftestCheck(),
        TruststoreCheck.asSelftestCheck(),
        SelfTestCheck("Unleash", false, unleashService::checkHealth)
    )

    private fun checkHealthStsService(): HealthCheckResult {
        return try {
            runBlocking {
                val systemUserAccessToken = stsService.getSystemUserAccessToken()
                requireNotNull(systemUserAccessToken) {
                    "Systemtoken var null"
                }
                HealthCheckResult.healthy()
            }
        } catch (e: Exception) {
            HealthCheckResult.unhealthy(e.message)
        }
    }

    private fun henvendelseService(): HenvendelseService {
        checkNotNull(henvendelsePortType) { "henvendelsePortType is null" }
        checkNotNull(innsynHenvendelsePortType) { "innsynHenvendelsePortType is null" }

        return HenvendelseService.Default(
            henvendelsePortType.port,
            innsynHenvendelsePortType.port
        )
    }

    private fun stsConfig(): StsConfig? {
        return StsConfig.builder()
            .url(configuration.SECURITYTOKENSERVICE_URL)
            .username(configuration.SRVMININNBOKS_USERNAME)
            .password(configuration.SRVMININNBOKS_PASSWORD)
            .build()
    }

    private fun systemUserTokenProvider(): SystemuserTokenProvider {
        return fromTokenEndpoint(
            configuration.STS_TOKENENDPOINT_URL,
            configuration.FSS_SRVMININNBOKS_USERNAME,
            configuration.FSS_SRVMININNBOKS_PASSWORD,
            configuration.STS_APIKEY
        )
    }
}
