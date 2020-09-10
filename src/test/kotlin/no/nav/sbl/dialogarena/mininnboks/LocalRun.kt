package no.nav.sbl.dialogarena.mininnboks

import org.slf4j.LoggerFactory

private val log = LoggerFactory.getLogger("mininnboks-api.LocalRun")

fun runLocally(useAuthentication: Boolean) {

    val applicationState = ApplicationState()


    val applicationServer = createHttpServer(
            applicationState = applicationState,
            port = 8081,
            configuration = Configuration(),
            useAuthentication = false
    )

    Runtime.getRuntime().addShutdownHook(Thread {
        log.info("Shutdown hook called, shutting down gracefully")
        applicationState.initialized = false
        applicationServer.stop(1000, 1000)
    })

    applicationServer.start(wait = true)
}

fun main() {
    runLocally(true)
}
