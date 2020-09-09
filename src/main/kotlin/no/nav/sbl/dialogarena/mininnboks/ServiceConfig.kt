package no.nav.sbl.dialogarena.mininnboks


import no.nav.common.cxf.CXFClient
import no.nav.common.cxf.StsConfig
import no.nav.common.rest.client.RestClient
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


class ServiceConfig(val configuration: Configuration) {


    fun personService(): PersonService {
        return PersonService.Default(personV3())
    }

    private fun personV3(): PersonV3 {
        return CXFClient<PersonV3>(PersonV3::class.java)
                .address(configuration.PERSON_V_3_URL)
                .configureStsForSubject(stsConfig())
                .build()
    }


    fun henvendelseService(personService: PersonService): HenvendelseService {
        return HenvendelseService.Default(
                henvendelse(),
                sendInnHenvendelse(),
                innsynHenvendesle(),
                personService
        )
    }


    fun stsConfig(): StsConfig? {
        return StsConfig.builder()
                .url(configuration.SECURITYTOKENSERVICE_URL)
                .username(configuration.SRVMININNBOKS_USERNAME)
                .password(configuration.SRVMININNBOKS_PASSWORD)
                .build()
    }


    fun systemUserTokenProvider(): SystemuserTokenProvider {
       // val stsApikey = EnvironmentUtils.getRequiredProperty(configuration.STS_APIKEY)
       // val client = RestUtils.createClient().register(ApigwRequestFilter(stsApikey))
        return fromTokenEndpoint(
                configuration.STS_TOKENENDPOINT_URL,
                configuration.FSS_SRVMININNBOKS_USERNAME,
               configuration.FSS_SRVMININNBOKS_PASSWORD,
                RestClient.baseClientBuilder().build()
        )
    }


    fun pdlService(stsService: SystemuserTokenProvider?): PdlService {
       // val pdlapiApikey = EnvironmentUtils.getRequiredProperty(configuration.PDL_API_APIKEY)
        //val client = RestClient.baseClient().register(ApigwRequestFilter(pdlapiApikey))
        return PdlService(RestClient.baseClientBuilder().build(), stsService!!, configuration)
    }


    /*fun pdlPing(pdlService: PdlService): PdlService {
        return pdlService.getHelsesjekk()
     }*/


    fun tilgangService(pdlService: PdlService?, personService: PersonService?): TilgangService {
        return TilgangServiceImpl(pdlService!!, personService!!)
    }


    // fun sendInnHenvendelsePing(): Pingable {
    //     return sendInnHenvendelse().helsesjekk!!
    // }

    private fun sendInnHenvendelse(): SendInnHenvendelsePortType {
        return CXFClient<SendInnHenvendelsePortType>(SendInnHenvendelsePortType::class.java)
                .address(configuration.SEND_INN_HENVENDELSE_WS_URL)
                .configureStsForSubject(stsConfig())
                .build()
    }


    /*  fun henvendelsePing(): Pingable {
          return henvendelse().helsesjekk!!
      }*/

    private fun henvendelse(): HenvendelsePortType {

        return CXFClient<HenvendelsePortType>(HenvendelsePortType::class.java)
                .address(configuration.HENVENDELSE_WS_URL)
                .configureStsForSubject(stsConfig())
                .build()
    }


/*
    fun innsynHenvendelsePing(): Pingable {
        return innsynHenvendesle().helsesjekk!!
    }
*/

    private fun innsynHenvendesle(): InnsynHenvendelsePortType {
        return CXFClient<InnsynHenvendelsePortType>(InnsynHenvendelsePortType::class.java)
                .address(configuration.INNSYN_HENVENDELSE_WS_URL)
                .configureStsForSubject(stsConfig())
                .build()

    }
}