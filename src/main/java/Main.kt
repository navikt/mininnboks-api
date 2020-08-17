import no.nav.apiapp.ApiApp
import no.nav.common.nais.utils.NaisUtils
import no.nav.sbl.dialogarena.mininnboks.config.ApplicationConfig
import no.nav.sbl.dialogarena.mininnboks.config.ServiceConfig
import no.nav.sbl.util.EnvironmentUtils

object Main {
    private const val DEFAULT_SECRETS_BASE_PATH = "/var/run/secrets/nais.io"

    @JvmStatic
    fun main(args: Array<String>) {
        loadVaultSecrets()
        loadApigwKeys()
        val serviceGatewayUrl = EnvironmentUtils.getRequiredProperty(ServiceConfig.SERVICEGATEWAY_URL)
        EnvironmentUtils.setProperty(ServiceConfig.INNSYN_HENVENDELSE_WS_URL, serviceGatewayUrl, EnvironmentUtils.Type.PUBLIC)
        EnvironmentUtils.setProperty(ServiceConfig.HENVENDELSE_WS_URL, serviceGatewayUrl, EnvironmentUtils.Type.PUBLIC)
        EnvironmentUtils.setProperty(ServiceConfig.SEND_INN_HENVENDELSE_WS_URL, serviceGatewayUrl, EnvironmentUtils.Type.PUBLIC)
        EnvironmentUtils.setProperty(ServiceConfig.PERSON_V_3_URL, serviceGatewayUrl, EnvironmentUtils.Type.PUBLIC)
        ApiApp.runApp(ApplicationConfig::class.java, args)
    }

    private fun loadVaultSecrets() {
        val fssServiceUser = NaisUtils.getCredentials("srvmininnboks-fss")
        EnvironmentUtils.setProperty(ApplicationConfig.FSS_SRVMININNBOKS_USERNAME, fssServiceUser.username, EnvironmentUtils.Type.PUBLIC)
        EnvironmentUtils.setProperty(ApplicationConfig.FSS_SRVMININNBOKS_PASSWORD, fssServiceUser.password, EnvironmentUtils.Type.SECRET)
        val serviceUser = NaisUtils.getCredentials("srvmininnboks")
        EnvironmentUtils.setProperty(ApplicationConfig.SRVMININNBOKS_USERNAME, serviceUser.username, EnvironmentUtils.Type.PUBLIC)
        EnvironmentUtils.setProperty(ApplicationConfig.SRVMININNBOKS_PASSWORD, serviceUser.password, EnvironmentUtils.Type.SECRET)
    }

    private fun loadApigwKeys() {
        EnvironmentUtils.setProperty(ServiceConfig.PDL_API_APIKEY, getApigwKey("pdl-api"), EnvironmentUtils.Type.SECRET)
        EnvironmentUtils.setProperty(ServiceConfig.STS_APIKEY, getApigwKey("security-token-service-token"), EnvironmentUtils.Type.SECRET)
    }

    private fun getApigwKey(producerApp: String): String {
        val location = String.format("%s/apigw/%s/x-nav-apiKey", DEFAULT_SECRETS_BASE_PATH, producerApp)
        return NaisUtils.getFileContent(location)
    }
}
