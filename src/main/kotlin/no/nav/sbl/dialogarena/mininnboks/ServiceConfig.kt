package no.nav.sbl.dialogarena.mininnboks

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
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
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService
import no.nav.sbl.dialogarena.mininnboks.consumer.saf.SafService
import no.nav.sbl.dialogarena.mininnboks.consumer.saf.SafServiceImpl
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider.Companion.fromTokenEndpoint
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangServiceImpl
import no.nav.sbl.dialogarena.mininnboks.consumer.tokendings.TokendingsService
import no.nav.sbl.dialogarena.mininnboks.consumer.tokendings.TokendingsServiceImpl
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import org.slf4j.MDC
import java.util.*

class ServiceConfig(val configuration: Configuration) {
    companion object {
        val ktorClient = HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = JacksonSerializer(JacksonUtils.objectMapper)
            }
//            install(TjenestekallLogging)
        }
    }

    val tokendingsService: TokendingsService = TokendingsServiceImpl(
        httpClient = ktorClient,
        configuration = configuration
    )

    val unleashService: UnleashService = UnleashServiceImpl(
        ByEnvironmentStrategy()
    )

    private val personV3 = portBuilder(
        PersonV3::class.java,
        configuration.PERSON_V_3_URL,
        "",
        stsConfig()
    )

    val henvendelsePortType = portBuilder(
        HenvendelsePortType::class.java,
        configuration.HENVENDELSE_WS_URL,
        "classpath:wsdl/Henvendelse.wsdl",
        stsConfig()
    )

    val sendInnHenvendelsePortType = portBuilder(
        SendInnHenvendelsePortType::class.java,
        configuration.SEND_INN_HENVENDELSE_WS_URL,
        "classpath:wsdl/SendInnHenvendelse.wsdl",
        stsConfig()
    )

    val innsynHenvendelsePortType = portBuilder(
        InnsynHenvendelsePortType::class.java,
        configuration.INNSYN_HENVENDELSE_WS_URL,
        "classpath:wsdl/InnsynHenvendelse.wsdl",
        stsConfig()
    )

    val personService = PersonService.Default(personV3.port)
    val henvendelseService = henvendelseService()
    val stsService = systemUserTokenProvider()
    val pdlService = pdlService(stsService)
    val safService: SafService = SafServiceImpl(
        client = ktorClient,
        tokendings = tokendingsService,
        configuration = configuration
    )
    val tilgangService = tilgangService(pdlService)
    val rateLimiterService = RateLimiterApiImpl(configuration.RATE_LIMITER_URL, ktorClient)

    val selfTestCheckStsService: SelfTestCheck = SelfTestCheck("Sjekker at systembruker kan hente token fra STS", true) {
        runBlocking {
            MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
            withContext(MDCContext()) {
                checkHealthStsService()
            }
        }
    }

    val selfTestCheckPersonV3: SelfTestCheck =
        portTypeSelfTestCheck("personV3") {
            personV3.pingPort.ping()
        }

    val selfTestCheckHenvendelse: SelfTestCheck = portTypeSelfTestCheck(
        "no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType"
    ) {
        henvendelsePortType.pingPort.ping()
    }

    val selfTestCheckSendInnHenvendelsePortType: SelfTestCheck = portTypeSelfTestCheck(
        "no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType"
    ) {
        sendInnHenvendelsePortType.pingPort.ping()
    }

    val selfTestCheckInnsynHenvendelsePortType: SelfTestCheck = portTypeSelfTestCheck(
        "no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType"
    ) {
        innsynHenvendelsePortType.pingPort.ping()
    }

    val selfTestChecklist = listOf(
        pdlService.selfTestCheck,
        tokendingsService.selftestCheck,
        selfTestCheckStsService,
        selfTestCheckPersonV3,
        selfTestCheckHenvendelse,
        selfTestCheckSendInnHenvendelsePortType,
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
            sendInnHenvendelsePortType.port,
            innsynHenvendelsePortType.port,
            personService,
            unleashService
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

    private fun pdlService(stsService: SystemuserTokenProvider): PdlService {
        return PdlService(ktorClient, stsService, configuration)
    }

    private fun tilgangService(pdlService: PdlService): TilgangService {
        return TilgangServiceImpl(pdlService, personService)
    }
}
