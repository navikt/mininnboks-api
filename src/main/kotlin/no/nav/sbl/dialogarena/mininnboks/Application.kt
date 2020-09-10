package no.nav.sbl.dialogarena.mininnboks

import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("mininnboks.Application")
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
