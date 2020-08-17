import no.nav.apiapp.ApiApp
import no.nav.common.nais.utils.NaisYamlUtils
import no.nav.sbl.dialogarena.mininnboks.config.ApplicationConfig
import no.nav.sbl.dialogarena.mininnboks.config.ServiceConfig
import no.nav.sbl.dialogarena.test.SystemProperties
import no.nav.sbl.util.EnvironmentUtils
import no.nav.testconfig.ApiAppTest
import java.util.*

object MainTest {
    private const val APPLICATION_NAME = "mininnboks-api"

    @JvmStatic
    fun main(args: Array<String>) {
        SystemProperties.setFrom(".vault.properties")
        NaisYamlUtils.loadFromYaml(NaisYamlUtils.getTemplatedConfig(".nais/qa-template.yaml", object : HashMap<String?, String?>() {
            init {
                put("namespace", "q0")
                put("image", "N/A")
                put("version", "N/A")
            }
        }))
        val serviceGatewayUrl = EnvironmentUtils.getRequiredProperty(ServiceConfig.SERVICEGATEWAY_URL)
        EnvironmentUtils.setProperty(ServiceConfig.INNSYN_HENVENDELSE_WS_URL, serviceGatewayUrl, EnvironmentUtils.Type.PUBLIC)
        EnvironmentUtils.setProperty(ServiceConfig.HENVENDELSE_WS_URL, serviceGatewayUrl, EnvironmentUtils.Type.PUBLIC)
        EnvironmentUtils.setProperty(ServiceConfig.SEND_INN_HENVENDELSE_WS_URL, serviceGatewayUrl, EnvironmentUtils.Type.PUBLIC)
        ApiAppTest.setupTestContext(ApiAppTest.Config.builder().applicationName(APPLICATION_NAME).build())
        ApiApp.runApp(ApplicationConfig::class.java, arrayOf("8455"))
    }
}
