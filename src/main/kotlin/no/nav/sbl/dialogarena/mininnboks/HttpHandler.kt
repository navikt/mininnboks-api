package no.nav.sbl.dialogarena.mininnboks

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.metrics.micrometer.*
import io.ktor.request.*
import io.ktor.response.*
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
import no.nav.sbl.dialogarena.mininnboks.JacksonUtils.Companion.objectMapper
import no.nav.sbl.dialogarena.mininnboks.provider.rest.dokument.dokumentController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.henvendelseController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.naisRoutes
import no.nav.sbl.dialogarena.mininnboks.provider.rest.resources.resourcesController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.sporsmalController
import org.slf4j.event.Level
import no.nav.sbl.dialogarena.mininnboks.JwtUtil.Companion as JwtUtil

val metricsRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

fun createHttpServer(
    applicationState: ApplicationState,
    configuration: Configuration,
    port: Int = 8080
): ApplicationEngine = embeddedServer(Netty, port) {
    installKtorFeatures(configuration)

    val serviceConfig = ServiceConfig(configuration)
    routing {
        authenticate {
            sporsmalController(serviceConfig.henvendelseService)
            henvendelseController(serviceConfig.henvendelseService)
            dokumentController(serviceConfig.safService)

            get("/tokendings") {
                val subject = requireNotNull(this.call.authentication.principal<SubjectPrincipal>())
                val token = subject.subject.ssoToken.token
                val exchangedToken = serviceConfig.tokendingsService.exchangeToken(token, configuration.SAF_CLIENT_ID)

                call.respond(exchangedToken)
            }
        }
        resourcesController()

        route("internal") {
            naisRoutes(
                readinessCheck = { applicationState.initialized },
                livenessCheck = { applicationState.running },
                selftestChecks = serviceConfig.selfTestChecklist
            )
        }
    }

    applicationState.initialized = true
}

private fun Application.installKtorFeatures(configuration: Configuration) {
    install(StatusPages) {
        notFoundHandler()
        exceptionHandler()
    }

    install(CORS) {
        anyHost()
        method(HttpMethod.Post)
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

    install(Authentication) {
        jwt {
            authHeader(JwtUtil::useJwtFromCookie)
            verifier(configuration.jwksUrl, configuration.jwtIssuer)
            validate { JwtUtil.validateJWT(this, it, configuration.LOGINSERVICE_IDPORTEN_AUDIENCE) }
        }
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(objectMapper))
    }

    install(XForwardedHeaderSupport)
    install(CallLogging) {
        level = Level.INFO
        filter { call -> !call.request.path().contains("/internal/isAlive") }
        filter { call -> !call.request.path().contains("/internal/isReady") }
        MDC.configure(this)
    }
}
