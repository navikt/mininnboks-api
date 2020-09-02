package no.nav.sbl.dialogarena.mininnboks

import no.nav.sbl.dialogarena.mininnboks.Configuration
import no.nav.sbl.dialogarena.mininnboks.config.utils.PortTypeUtils
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
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

class ServiceConfig(val configuration: Configuration) {


    fun personService(): PersonService {
        return PersonService.Default(personV3().port!!)
    }


    fun personV3Ping(): Pingable {
        return personV3().helsesjekk!!
    }

    private fun personV3(): PortTypeUtils.PortType<PersonV3> {
        return PortTypeUtils.createPortType(configuration.PERSON_V_3_URL,
        "",
        PersonV3::class.java,PersonV3::ping)
    }


    fun henvendelseService(personService: PersonService): HenvendelseService {
        return HenvendelseService.Default(
                henvendelse().port!!,
                sendInnHenvendelse().port!!,
                innsynHenvendesle().port!!,
                personService
        )
    }


    fun systemUserTokenProvider(): SystemuserTokenProvider {
        val stsApikey = EnvironmentUtils.getRequiredProperty(configuration.STS_APIKEY)
        val client = RestUtils.createClient().register(ApigwRequestFilter(stsApikey))
        return fromTokenEndpoint(
                EnvironmentUtils.getRequiredProperty(configuration.STS_TOKENENDPOINT_URL),
                EnvironmentUtils.getRequiredProperty(configuration.FSS_SRVMININNBOKS_USERNAME),
                EnvironmentUtils.getRequiredProperty(configuration.FSS_SRVMININNBOKS_PASSWORD),
                client
        )
    }


    fun pdlService(stsService: SystemuserTokenProvider?): PdlService {
        val pdlapiApikey = EnvironmentUtils.getRequiredProperty(configuration.PDL_API_APIKEY)
        val client = RestUtils.createClient().register(ApigwRequestFilter(pdlapiApikey))
        return PdlServiceImpl(client, stsService!!, configuration)
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
                EnvironmentUtils.getRequiredProperty(configuration.SEND_INN_HENVENDELSE_WS_URL),
                "classpath:wsdl/SendInnHenvendelse.wsdl",
                SendInnHenvendelsePortType::class.java) { obj: SendInnHenvendelsePortType -> obj.ping() }
    }


    fun henvendelsePing(): Pingable {
        return henvendelse().helsesjekk!!
    }

    private fun henvendelse(): PortTypeUtils.PortType<HenvendelsePortType> {
        return PortTypeUtils.createPortType(
                EnvironmentUtils.getRequiredProperty(configuration.HENVENDELSE_WS_URL),
                "classpath:wsdl/Henvendelse.wsdl",
                HenvendelsePortType::class.java) { obj: HenvendelsePortType -> obj.ping() }
    }


    fun innsynHenvendelsePing(): Pingable {
        return innsynHenvendesle().helsesjekk!!
    }

    private fun innsynHenvendesle(): PortTypeUtils.PortType<InnsynHenvendelsePortType> {
        return PortTypeUtils.createPortType(
                EnvironmentUtils.getRequiredProperty(configuration.INNSYN_HENVENDELSE_WS_URL),
                "classpath:wsdl/InnsynHenvendelse.wsdl",
                InnsynHenvendelsePortType::class.java) { obj -> obj. ping() }
    }


}
