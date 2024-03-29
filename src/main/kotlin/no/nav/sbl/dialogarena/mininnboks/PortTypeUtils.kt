package no.nav.sbl.dialogarena.mininnboks

import kotlinx.coroutines.runBlocking
import no.nav.common.cxf.CXFClient
import no.nav.common.cxf.StsConfig
import no.nav.common.health.HealthCheckResult
import no.nav.common.health.selftest.SelfTestCheck
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe

object PortUtils {

    fun <T> portBuilder(clazz: Class<T>, address: String, wsdlUrl: String, stsConfig: StsConfig?): PortType<T> {
        return PortType(
            cxfClientBuilder(clazz, address, wsdlUrl).configureStsForSubject(stsConfig).build(),
            cxfClientBuilder(clazz, address, wsdlUrl).configureStsForSystemUser(stsConfig).build()
        )
    }

    fun portTypeSelfTestCheck(portTypeName: String, block: () -> Unit): SelfTestCheck {
        return SelfTestCheck(portTypeName, false) {
            try {
                runBlocking {
                    block()
                }
                return@SelfTestCheck HealthCheckResult.healthy()
            } catch (e: Exception) {
                return@SelfTestCheck HealthCheckResult.unhealthy("$portTypeName feilet ${e.message}")
            }
        }
    }

    private fun <T> cxfClientBuilder(t: Class<T>, address: String, wsdlUrl: String): CXFClient<T> {
        return CXFClient<T>(t)
            .address(address)
            .wsdl(wsdlUrl)
            .timeout(5_000, 20_000)
            .withProperty(
                "jaxb.additionalContextClasses",
                arrayOf<Class<*>>(
                    XMLHenvendelse::class.java,
                    XMLMetadataListe::class.java,
                    XMLMeldingFraBruker::class.java,
                    XMLMeldingTilBruker::class.java
                )
            )
    }
}

data class PortType<T>(val port: T, val pingPort: T)
