package no.nav.sbl.dialogarena.mininnboks

import io.ktor.application.*
import io.ktor.locations.Locations
import io.ktor.routing.*
import no.nav.sbl.dialogarena.mininnboks.config.ServiceConfig
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseServiceImpl
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangServiceImpl
import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.HenvendelseController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.resources.ResourcesController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang.TilgangController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.SporsmalController

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads

fun Application.module(testing: Boolean = false) {

    install(Locations)

    var serviceConfig = ServiceConfig()
    val henvendelseService = serviceConfig.henvendelseService(serviceConfig.personService())

    val tilgangService = serviceConfig.tilgangService(serviceConfig.pdlService(serviceConfig.systemUserTokenProvider()),
            serviceConfig.personService())


    routing {

        SporsmalController(henvendelseService)
        HenvendelseService.HenvendelseController(henvendelseService, tilgangService)
        TilgangController(tilgangService)
        ResourcesController()

    }
}
