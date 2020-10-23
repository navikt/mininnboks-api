package no.nav.sbl.dialogarena.mininnboks

import no.nav.common.utils.EnvironmentUtils
import no.nav.common.utils.NaisUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val log: Logger = LoggerFactory.getLogger("mininnboks.Application")

data class ApplicationState(var running: Boolean = true, var initialized: Boolean = false)

const val FSS_SRVMININNBOKS_USERNAME = "FSS_SRVMININNBOKS_USERNAME"
const val FSS_SRVMININNBOKS_PASSWORD = "FSS_SRVMININNBOKS_PASSWORD"
const val SRVMININNBOKS_USERNAME = "SRVMININNBOKS_USERNAME"
const val SRVMININNBOKS_PASSWORD = "SRVMININNBOKS_PASSWORD"
const val PDL_API_APIKEY = "PDL_API_APIKEY"
const val STS_APIKEY = "STS_APIKEY"
private const val DEFAULT_SECRETS_BASE_PATH = "/var/run/secrets/nais.io"


fun main() {
    loadVaultSecrets()
    loadApigwKeys()
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

private fun loadVaultSecrets() {
    val fssServiceUser = NaisUtils.getCredentials("srvmininnboks-fss")
    EnvironmentUtils.setProperty(FSS_SRVMININNBOKS_USERNAME, fssServiceUser.username, EnvironmentUtils.Type.PUBLIC)
    EnvironmentUtils.setProperty(FSS_SRVMININNBOKS_PASSWORD, fssServiceUser.password, EnvironmentUtils.Type.SECRET)
    val serviceUser = NaisUtils.getCredentials("srvmininnboks")
    EnvironmentUtils.setProperty(SRVMININNBOKS_USERNAME, serviceUser.username, EnvironmentUtils.Type.PUBLIC)
    EnvironmentUtils.setProperty(SRVMININNBOKS_PASSWORD, serviceUser.password, EnvironmentUtils.Type.SECRET)
}

private fun loadApigwKeys() {
    EnvironmentUtils.setProperty(PDL_API_APIKEY, getApigwKey("pdl-api"), EnvironmentUtils.Type.SECRET)
    EnvironmentUtils.setProperty(STS_APIKEY, getApigwKey("security-token-service-token"), EnvironmentUtils.Type.SECRET)
}

private fun getApigwKey(producerApp: String): String? {
    val location = "%s/apigw/%s/x-nav-apiKey".format(DEFAULT_SECRETS_BASE_PATH, producerApp)
    return NaisUtils.getFileContent(location)
}
