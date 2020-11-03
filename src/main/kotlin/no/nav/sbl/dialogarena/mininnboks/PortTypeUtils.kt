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
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType

object PortUtils {

    fun <T> portBuilder(t: Class<T>, address: String, wsdlUrl: String, stsConfig: StsConfig?): PortType {
        return PortType(cxfClientBuilder(t, address, wsdlUrl, stsConfig)?.configureStsForSubject(stsConfig),
                cxfClientBuilder(t, address, wsdlUrl, stsConfig)?.configureStsForSystemUser(stsConfig))

    }

    fun portTypeSelfTestCheck(portTypeName: String, block: () -> Unit): SelfTestCheck {
        return SelfTestCheck(portTypeName, false) {
            try {
                runBlocking {
                    externalCall(KtorUtils.dummySubject()) {
                        //(portType.t?.build() as HenvendelsePortType).ping()
                        block()
                    }
                }
                return@SelfTestCheck HealthCheckResult.healthy()
            } catch (e: Exception) {
                return@SelfTestCheck HealthCheckResult.unhealthy("$portTypeName feilet ${e.message}")
            }
        }
    }

    private fun <T> cxfClientBuilder(t: Class<T>, address: String, wsdlUrl: String, stsConfig: StsConfig?): CXFClient<T>? {
        return CXFClient<T>(t)
                .address(address)
                .wsdl(wsdlUrl)
                .timeout(5_000, 20_000)
                .withProperty("jaxb.additionalContextClasses", arrayOf<Class<*>>(
                        XMLHenvendelse::class.java,
                        XMLMetadataListe::class.java,
                        XMLMeldingFraBruker::class.java,
                        XMLMeldingTilBruker::class.java)
                )
    }



}

data class PortType(val s: CXFClient<*>?,val t: CXFClient<*>?)

