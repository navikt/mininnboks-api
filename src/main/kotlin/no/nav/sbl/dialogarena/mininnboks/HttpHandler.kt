package no.nav.sbl.dialogarena.mininnboks

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.metrics.micrometer.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import no.nav.sbl.dialogarena.mininnboks.ObjectMapperProvider.Companion.objectMapper
import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.henvendelseController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.naisRoutes
import no.nav.sbl.dialogarena.mininnboks.provider.rest.resources.resourcesController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang.tilgangController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.sporsmalController
import org.slf4j.event.Level
import no.nav.sbl.dialogarena.mininnboks.JwtUtil.Companion as JwtUtil

val metricsRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

fun createHttpServer(applicationState: ApplicationState,
                     configuration: Configuration,
                     port: Int = 8080,
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

    install(MicrometerMetrics) {
        registry = metricsRegistry
        meterBinders = listOf(
                ClassLoaderMetrics(),
                JvmMemoryMetrics(),
                JvmGcMetrics(),
                ProcessorMetrics(),
                JvmThreadMetrics()
        )
    }

    if (useAuthentication) {
        install(Authentication) {
            jwt {
                authHeader(JwtUtil::useJwtFromCookie)
                verifier(configuration.jwksUrl, configuration.jwtIssuer)
                validate { JwtUtil.validateJWT(this, it, configuration.AAD_B2C_CLIENTID_USERNAME) }
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
    routing {
        sporsmalController(serviceConfig.henvendelseService, true)
        henvendelseController(serviceConfig.henvendelseService, serviceConfig.tilgangService, true)
        tilgangController(serviceConfig.tilgangService, true)
        resourcesController()

        val selfTestChecklist = listOf(
                serviceConfig.pdlService.selfTestCheck
        )

        route("internal") {
            naisRoutes(readinessCheck = { applicationState.initialized },
                    livenessCheck = { applicationState.running },
                    selftestChecks = selfTestChecklist)
        }
    }

    applicationState.initialized = true
}

