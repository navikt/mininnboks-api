package no.nav.sbl.dialogarena.mininnboks

import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.jackson.JacksonConverter
import io.ktor.request.path
import io.ktor.server.netty.Netty
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.routing.routing
import no.nav.sbl.dialogarena.mininnboks.ObjectMapperProvider.Companion.objectMapper
import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.HenvendelseController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.resources.ResourcesController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang.TilgangController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.sporsmalController

import org.slf4j.event.Level
import no.nav.sbl.dialogarena.mininnboks.JwtUtil.Companion as JwtUtil

fun createHttpServer(applicationState: ApplicationState,
                     configuration: Configuration,
                     port: Int = 8081,
                     useAuthentication: Boolean): ApplicationEngine = embeddedServer(Netty, port) {

    install(StatusPages) {
        notFoundHandler()
        exceptionHandler()
    }

    install(CORS) {
        anyHost()
        method(HttpMethod.Post)
        method(HttpMethod.Delete)
    }

    if (useAuthentication) {
        install(Authentication) {
            jwt {
                authHeader(JwtUtil::useJwtFromCookie)
                verifier(configuration.jwksUrl, configuration.jwtIssuer)
                validate { JwtUtil.validateJWT(it) }
            }
        }
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(objectMapper))
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().contains("/") }
    }

    val serviceConfig = ServiceConfig(configuration)
    val henvendelseService = serviceConfig.henvendelseService(serviceConfig.personService())

    val tilgangService = serviceConfig.tilgangService(serviceConfig.pdlService(serviceConfig.systemUserTokenProvider()),
            serviceConfig.personService())

    routing {
        sporsmalController(henvendelseService, true)
        HenvendelseController(henvendelseService, tilgangService, true)
        TilgangController(tilgangService)
        ResourcesController()
    }

    applicationState.initialized = true

}
