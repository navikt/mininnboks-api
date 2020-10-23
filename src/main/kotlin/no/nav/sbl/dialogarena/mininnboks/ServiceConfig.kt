package no.nav.sbl.dialogarena.mininnboks


import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import no.nav.common.cxf.StsConfig
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.sbl.dialogarena.mininnboks.PortUtils.portBuilder
import no.nav.sbl.dialogarena.mininnboks.PortUtils.portTypeSelfTestCheck
import no.nav.sbl.dialogarena.mininnboks.common.DiskCheck
import no.nav.sbl.dialogarena.mininnboks.common.TruststoreCheck
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider.Companion.fromTokenEndpoint
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangServiceImpl
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import org.slf4j.MDC
import java.util.*


class ServiceConfig(val configuration: Configuration) {

    private val personV3 = personV3()
    val personService = PersonService.Default(personV3?.s?.build() as PersonV3)

    val henvendelsePortType = portBuilder(HenvendelsePortType::class.java,
            configuration.HENVENDELSE_WS_URL,
            "classpath:wsdl/Henvendelse.wsdl",
            stsConfig()
    )

    val sendInnHenvendelsePortType = portBuilder(SendInnHenvendelsePortType::class.java,
            configuration.SEND_INN_HENVENDELSE_WS_URL,
            "classpath:wsdl/SendInnHenvendelse.wsdl",
            stsConfig()
    )

    val innsynHenvendelsePortType = portBuilder(InnsynHenvendelsePortType::class.java,
            configuration.INNSYN_HENVENDELSE_WS_URL,
            "classpath:wsdl/InnsynHenvendelse.wsdl",
            stsConfig()
    )

    val henvendelseService = henvendelseService()
    val stsService = systemUserTokenProvider()
    val pdlService = pdlService(stsService)
    val tilgangService = tilgangService(pdlService)

    val selfTestCheckStsService: SelfTestCheck = SelfTestCheck("Sjekker at systembruker kan hente token fra STS", true) {
        runBlocking {
            MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
            withContext(MDCContext()) {
                checkHealthStsService()
            }
        }
    }

    val selfTestCheckPersonV3: SelfTestCheck? = personV3?.let {
        portTypeSelfTestCheck("personV3") {
            (it.t?.build() as PersonV3).ping()
        }
    }

    val selfTestCheckHenvendelse: SelfTestCheck? = portTypeSelfTestCheck(
            "no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType") {
        (henvendelsePortType.t?.build() as HenvendelsePortType).ping()
    }

    val selfTestCheckSendInnHenvendelsePortType: SelfTestCheck? = portTypeSelfTestCheck(
            "no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType") {
        (sendInnHenvendelsePortType.t?.build() as SendInnHenvendelsePortType).ping()
    }

    val selfTestCheckInnsynHenvendelsePortType: SelfTestCheck? = portTypeSelfTestCheck(
            "no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType") {
        (innsynHenvendelsePortType.t?.build() as InnsynHenvendelsePortType).ping()
    }

    val selfTestChecklist = listOf(
            pdlService.selfTestCheck,
            selfTestCheckStsService,
            selfTestCheckPersonV3,
            selfTestCheckHenvendelse,
            selfTestCheckSendInnHenvendelsePortType,
            selfTestCheckInnsynHenvendelsePortType,
            DiskCheck.asSelftestCheck(),
            TruststoreCheck.asSelftestCheck()
    )

    private fun checkHealthStsService(): HealthCheckResult {
        runCatching {
            stsService.getSystemUserAccessToken()
        }.onSuccess {
            if (it != null)
                return HealthCheckResult.healthy()
            else
                return HealthCheckResult.unhealthy("Null response")
        }.onFailure {
            return HealthCheckResult.unhealthy(it.message)
        }
        return HealthCheckResult.unhealthy("Feil ved Helsesjekk")
    }

    private fun personV3(): PortType? {
        return portBuilder(PersonV3::class.java,
                configuration.PERSON_V_3_URL,
                "",
                stsConfig()
        )
    }

    private fun henvendelseService(): HenvendelseService {
        checkNotNull(henvendelsePortType) { "henvendelsePortType is null" }
        checkNotNull(innsynHenvendelsePortType) { "innsynHenvendelsePortType is null" }

        return HenvendelseService.Default(
                henvendelsePortType.s?.build() as HenvendelsePortType,
                sendInnHenvendelsePortType!!.s?.build() as SendInnHenvendelsePortType,
                innsynHenvendelsePortType.s?.build() as InnsynHenvendelsePortType,
                personService
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
                configuration.STS_APIKEY,
                RestClient.baseClientBuilder().build()
        )
    }

    private fun pdlService(stsService: SystemuserTokenProvider): PdlService {
        return PdlService(RestClient.baseClientBuilder().build(), stsService, configuration)
    }

    private fun tilgangService(pdlService: PdlService): TilgangService {
        return TilgangServiceImpl(pdlService, personService)
    }
}
