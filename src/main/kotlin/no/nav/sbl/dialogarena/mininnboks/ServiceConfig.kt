package no.nav.sbl.dialogarena.mininnboks


import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import no.nav.common.cxf.CXFClient
import no.nav.common.cxf.StsConfig
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.log.MDCConstants
import no.nav.common.rest.client.RestClient
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProviderImpl
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
    val personService = PersonService.Default(personV3)
    val henvendelsePortType = henvendelse()
    val henvendelseService = henvendelseService()
    val stsService = systemUserTokenProvider()
    val pdlService = pdlService(stsService)
    val tilgangService = tilgangService(pdlService)

    val selfTestCheckStsService: SelfTestCheck = SelfTestCheck(configuration.SECURITYTOKENSERVICE_URL, true) {
        runBlocking {
            MDC.put(MDCConstants.MDC_CALL_ID, UUID.randomUUID().toString())
            withContext(MDCContext()) {
                checkHealthStsService()
            }
        }
    }

    val selfTestCheckHenvendelse: SelfTestCheck = SelfTestCheck(configuration.HENVENDELSE_WS_URL, true) {
        try {
            runBlocking {
                externalCall(KtorUtils.dummySubject()) {
                    henvendelsePortType.ping()
                }
            }
            return@SelfTestCheck HealthCheckResult.healthy()
        } catch (e: Exception) {
            return@SelfTestCheck HealthCheckResult.unhealthy("henvendelse feilet ${e.message}")
        }
    }

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

    private fun personV3(): PersonV3 {
        return CXFClient<PersonV3>(PersonV3::class.java)
                .address(configuration.PERSON_V_3_URL)
                .configureStsForSubject(stsConfig())
                .build()
    }

    private fun henvendelseService(): HenvendelseService {
        checkNotNull(henvendelsePortType) { "henvendelsePortType is null" }
        return HenvendelseService.Default(
                henvendelsePortType,
                sendInnHenvendelse(),
                innsynHenvendesle(),
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
        return SystemuserTokenProviderImpl(
                true,
                configuration.LOGINSERVICE_IDPORTEN_DISCOVERY_URL,
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

    private fun sendInnHenvendelse(): SendInnHenvendelsePortType {
        return CXFClient<SendInnHenvendelsePortType>(SendInnHenvendelsePortType::class.java)
                .address(configuration.SEND_INN_HENVENDELSE_WS_URL)
                .wsdl("classpath:wsdl/SendInnHenvendelse.wsdl")
                .timeout(5_000, 20_000)
                .withProperty("jaxb.additionalContextClasses", arrayOf<Class<*>>(
                        XMLHenvendelse::class.java,
                        XMLMetadataListe::class.java,
                        XMLMeldingFraBruker::class.java,
                        XMLMeldingTilBruker::class.java)
                )
                .configureStsForSubject(stsConfig())
                .build()
    }

    private fun henvendelse(): HenvendelsePortType {
        return CXFClient<HenvendelsePortType>(HenvendelsePortType::class.java)
                .address(configuration.HENVENDELSE_WS_URL)
                .wsdl("classpath:wsdl/Henvendelse.wsdl")
                .configureStsForSubject(stsConfig())
                .timeout(5_000, 20_000)
                .withProperty("jaxb.additionalContextClasses", arrayOf<Class<*>>(
                        XMLHenvendelse::class.java,
                        XMLMetadataListe::class.java,
                        XMLMeldingFraBruker::class.java,
                        XMLMeldingTilBruker::class.java)
                )
                .build()
    }

    private fun innsynHenvendesle(): InnsynHenvendelsePortType {
        return CXFClient<InnsynHenvendelsePortType>(InnsynHenvendelsePortType::class.java)
                .address(configuration.INNSYN_HENVENDELSE_WS_URL)
                .wsdl("classpath:wsdl/InnsynHenvendelse.wsdl")
                .configureStsForSubject(stsConfig())
                .withProperty("jaxb.additionalContextClasses", arrayOf<Class<*>>(
                        XMLHenvendelse::class.java,
                        XMLMetadataListe::class.java,
                        XMLMeldingFraBruker::class.java,
                        XMLMeldingTilBruker::class.java)
                )
                .timeout(5_000, 20_000)
                .build()

    }
}
