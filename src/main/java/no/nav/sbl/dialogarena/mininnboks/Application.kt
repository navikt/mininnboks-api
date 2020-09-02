package no.nav.sbl.dialogarena.mininnboks

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.jwt.jwt
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.jackson.*
import io.ktor.locations.Locations
import io.ktor.request.path
import io.ktor.routing.routing
import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.HenvendelseController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.resources.ResourcesController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang.TilgangController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.sporsmalController
import no.nav.sbl.dialogarena.mininnboks.ObjectMapperProvider.Companion.objectMapper
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

import no.nav.sbl.dialogarena.mininnboks.JwtUtil.Companion as JwtUtil


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.mininboks(testing: Boolean = false) {

    val log = LoggerFactory.getLogger("rate-limiter.Application")

    val configuration = Configuration()

    install(StatusPages) {
        notFoundHandler()
        exceptionHandler()
    }

    install(ContentNegotiation) {
        jackson {
            register(ContentType.Application.Json, JacksonConverter(objectMapper))
        }
    }

    install(CORS) {
        anyHost()
        method(HttpMethod.Post)
        method(HttpMethod.Delete)
    }

    install(Locations)


    install(io.ktor.auth.Authentication) {
        jwt {
            authHeader(JwtUtil::useJwtFromCookie)
            verifier(configuration.jwksUrl, configuration.jwtIssuer)
            validate { JwtUtil.validateJWT(it) }
        }
    }


    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().contains("/api/") }
    }


    var serviceConfig = ServiceConfig(configuration)
    val henvendelseService = serviceConfig.henvendelseService(serviceConfig.personService())

    val tilgangService = serviceConfig.tilgangService(serviceConfig.pdlService(serviceConfig.systemUserTokenProvider()),
            serviceConfig.personService())



    routing {

        sporsmalController(henvendelseService, true)
        HenvendelseController(henvendelseService, tilgangService)
        TilgangController(tilgangService)
        ResourcesController()

    }
}
