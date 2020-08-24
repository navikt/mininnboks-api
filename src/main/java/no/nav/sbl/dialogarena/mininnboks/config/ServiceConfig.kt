package no.nav.sbl.dialogarena.mininnboks.config

import no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseServiceImpl
import no.nav.sbl.dialogarena.mininnboks.consumer.PersonService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlService
import no.nav.sbl.dialogarena.mininnboks.consumer.pdl.PdlServiceImpl
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider
import no.nav.sbl.dialogarena.mininnboks.consumer.sts.SystemuserTokenProvider.Companion.fromTokenEndpoint
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangServiceImpl
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.ApigwRequestFilter
import no.nav.sbl.dialogarena.types.Pingable
import no.nav.sbl.rest.RestUtils
import no.nav.sbl.util.EnvironmentUtils
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType
import no.nav.tjeneste.virksomhet.person.v3.binding.PersonV3
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

class ServiceConfig {

    fun personService(): PersonService {
        return PersonService.Default(personV3().port!!)
    }


    fun personV3Ping(): Pingable {
        return personV3().helsesjekk!!
    }

    private fun personV3(): PortTypeUtils.PortType<PersonV3> {
        return PortTypeUtils.createPortType(EnvironmentUtils.getRequiredProperty(PERSON_V_3_URL),
                "",
                PersonV3::class.java) { obj -> obj.ping() }
    }


    fun henvendelseService(personService: PersonService): HenvendelseService {
        return HenvendelseServiceImpl(
                henvendelse().port!!,
                sendInnHenvendelse().port!!,
                innsynHenvendesle().port!!,
                personService
        )
    }


    fun systemUserTokenProvider(): SystemuserTokenProvider {
        val stsApikey = EnvironmentUtils.getRequiredProperty(STS_APIKEY)
        val client = RestUtils.createClient().register(ApigwRequestFilter(stsApikey))
        return fromTokenEndpoint(
                EnvironmentUtils.getRequiredProperty(STS_TOKENENDPOINT_URL),
                EnvironmentUtils.getRequiredProperty(ApplicationConfig.FSS_SRVMININNBOKS_USERNAME),
                EnvironmentUtils.getRequiredProperty(ApplicationConfig.FSS_SRVMININNBOKS_PASSWORD),
                client
        )
    }


    fun pdlService(stsService: SystemuserTokenProvider?): PdlService {
        val pdlapiApikey = EnvironmentUtils.getRequiredProperty(PDL_API_APIKEY)
        val client = RestUtils.createClient().register(ApigwRequestFilter(pdlapiApikey))
        return PdlServiceImpl(client, stsService!!)
    }


    fun pdlPing(pdlService: PdlService): Pingable {
        return pdlService.getHelsesjekk()
    }


    fun tilgangService(pdlService: PdlService?, personService: PersonService?): TilgangService {
        return TilgangServiceImpl(pdlService!!, personService!!)
    }


    fun sendInnHenvendelsePing(): Pingable {
        return sendInnHenvendelse().helsesjekk!!
    }

    private fun sendInnHenvendelse(): PortTypeUtils.PortType<SendInnHenvendelsePortType> {
        return PortTypeUtils.createPortType(
                EnvironmentUtils.getRequiredProperty(SEND_INN_HENVENDELSE_WS_URL),
                "classpath:wsdl/SendInnHenvendelse.wsdl",
                SendInnHenvendelsePortType::class.java) { obj: SendInnHenvendelsePortType -> obj.ping() }
    }


    fun henvendelsePing(): Pingable {
        return henvendelse().helsesjekk!!
    }

    private fun henvendelse(): PortTypeUtils.PortType<HenvendelsePortType> {
        return PortTypeUtils.createPortType(
                EnvironmentUtils.getRequiredProperty(HENVENDELSE_WS_URL),
                "classpath:wsdl/Henvendelse.wsdl",
                HenvendelsePortType::class.java) { obj: HenvendelsePortType -> obj.ping() }
    }


    fun innsynHenvendelsePing(): Pingable {
        return innsynHenvendesle().helsesjekk!!
    }

    private fun innsynHenvendesle(): PortTypeUtils.PortType<InnsynHenvendelsePortType> {
        return PortTypeUtils.createPortType(
                EnvironmentUtils.getRequiredProperty(INNSYN_HENVENDELSE_WS_URL),
                "classpath:wsdl/InnsynHenvendelse.wsdl",
                InnsynHenvendelsePortType::class.java) { obj -> obj. ping() }
    }

    companion object {
        const val SERVICEGATEWAY_URL = "SERVICEGATEWAY_URL"
        const val INNSYN_HENVENDELSE_WS_URL = "innsyn.henvendelse.ws.url"
        const val HENVENDELSE_WS_URL = "henvendelse.ws.url"
        const val SEND_INN_HENVENDELSE_WS_URL = "send.inn.henvendelse.ws.url"
        const val PERSON_V_3_URL = "person.v3.url"
        const val PDL_API_URL = "PDL_API_URL"
        const val PDL_API_APIKEY = "PDL_API_APIKEY"
        const val STS_APIKEY = "STS_APIKEY"
        const val STS_TOKENENDPOINT_URL = "STS_TOKENENDPOINT_URL"
    }
}
