package no.nav.sbl.dialogarena.mininnboks

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.metrics.dropwizard.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.prometheus.client.dropwizard.DropwizardExports
import no.nav.common.utils.EnvironmentUtils
import no.nav.common.utils.EnvironmentUtils.setProperty
import no.nav.common.utils.NaisUtils
import no.nav.sbl.dialogarena.mininnboks.ObjectMapperProvider.Companion.objectMapper
import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.henvendelseController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.naisRoutes
import no.nav.sbl.dialogarena.mininnboks.provider.rest.resources.resourcesController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang.tilgangController
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.sporsmalController
import org.slf4j.event.Level
import no.nav.sbl.dialogarena.mininnboks.JwtUtil.Companion as JwtUtil

const val FSS_SRVMININNBOKS_USERNAME = "FSS_SRVMININNBOKS_USERNAME"
const val FSS_SRVMININNBOKS_PASSWORD = "FSS_SRVMININNBOKS_PASSWORD"
const val SRVMININNBOKS_USERNAME = "SRVMININNBOKS_USERNAME"
const val SRVMININNBOKS_PASSWORD = "SRVMININNBOKS_PASSWORD"
const val PDL_API_URL = "PDL_API_URL"
const val PDL_API_APIKEY = "PDL_API_APIKEY"
const val STS_APIKEY = "STS_APIKEY"
private const val DEFAULT_SECRETS_BASE_PATH = "/var/run/secrets/nais.io"


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

    if (useAuthentication) {
        install(Authentication) {
            jwt {
                authHeader(JwtUtil::useJwtFromCookie)
                verifier(configuration.jwksUrl, configuration.jwtIssuer)
                validate { JwtUtil.validateJWT(this, it) }
            }
        }
    }

    install(DropwizardMetrics) {
        io.prometheus.client.CollectorRegistry.defaultRegistry.register(DropwizardExports(registry))
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(objectMapper))
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().contains("/") }
    }


    loadVaultSecrets()
    loadApigwKeys()
    val serviceConfig = ServiceConfig(configuration)
    val henvendelseService = serviceConfig.henvendelseService(serviceConfig.personService())

    val tilgangService = serviceConfig.tilgangService(serviceConfig.pdlService(serviceConfig.systemUserTokenProvider()),
            serviceConfig.personService())

    routing {
            sporsmalController(henvendelseService, true)
            henvendelseController(henvendelseService, tilgangService, true)
            tilgangController(tilgangService)
            resourcesController()

            route("internal") {
                naisRoutes(readinessCheck = { applicationState.initialized }, livenessCheck = { applicationState.running })
            }
    }

    applicationState.initialized = true
}

private fun loadVaultSecrets() {
    val fssServiceUser = NaisUtils.getCredentials("srvmininnboks-fss")
    setProperty(FSS_SRVMININNBOKS_USERNAME, fssServiceUser.username, EnvironmentUtils.Type.PUBLIC)
    setProperty(FSS_SRVMININNBOKS_PASSWORD, fssServiceUser.password, EnvironmentUtils.Type.SECRET)
    val serviceUser = NaisUtils.getCredentials("srvmininnboks")
    setProperty(SRVMININNBOKS_USERNAME, serviceUser.username, EnvironmentUtils.Type.PUBLIC)
    setProperty(SRVMININNBOKS_PASSWORD, serviceUser.password, EnvironmentUtils.Type.SECRET)
}

private fun loadApigwKeys() {
    setProperty(PDL_API_APIKEY, getApigwKey("pdl-api"), EnvironmentUtils.Type.SECRET)
    setProperty(STS_APIKEY, getApigwKey("security-token-service-token"), EnvironmentUtils.Type.SECRET)
}

private fun getApigwKey(producerApp: String): String? {
    val location = java.lang.String.format("%s/apigw/%s/x-nav-apiKey", DEFAULT_SECRETS_BASE_PATH, producerApp)
    return NaisUtils.getFileContent(location)
}
