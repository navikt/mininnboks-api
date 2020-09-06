package no.nav.sbl.dialogarena.mininnboks


import org.slf4j.LoggerFactory

val log = LoggerFactory.getLogger("mininnboks.Application")
data class ApplicationState(var running: Boolean = true, var initialized: Boolean = false)

fun main() {
    val configuration = Configuration()
    val applicationState = ApplicationState()

    val applicationServer = createHttpServer(
            applicationState = applicationState,
            configuration = configuration,
            useAuthentication = true
    )

    Runtime.getRuntime().addShutdownHook(Thread {
        log.info("Shutdown hook called, shutting down gracefully")
        applicationState.initialized = false
        applicationServer.stop(5000, 5000)
    })

    applicationServer.start(wait = true)
}

/*
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.mininnboks(testing: Boolean = false) {

   // val log = LoggerFactory.getLogger("mininnboks.Application")

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
        filter { call -> call.request.path().contains("/") }
    }


    val serviceConfig = ServiceConfig(configuration)
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
*/
