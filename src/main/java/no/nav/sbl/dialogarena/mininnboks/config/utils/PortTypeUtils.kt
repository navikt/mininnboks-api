package no.nav.sbl.dialogarena.mininnboks.config.utils

import lombok.Builder
import no.nav.apiapp.selftest.Helsesjekk
import no.nav.apiapp.selftest.HelsesjekkMetadata
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe
import no.nav.sbl.dialogarena.common.cxf.CXFClient
import no.nav.sbl.util.fn.UnsafeConsumer

typealias Consumer<T> = (T) -> Unit

object PortTypeUtils {
    fun <T> createPortType(address: String, wsdlUrl: String, serviceClass: Class<T>, ping: Consumer<T>): PortType<T> {
        val portType = clientBuilder(address, serviceClass, wsdlUrl).configureStsForOnBehalfOfWithJWT().build()
        val pingPort = clientBuilder(address, serviceClass, wsdlUrl).configureStsForSystemUser().build()
        val helsesjekkMetadata = HelsesjekkMetadata(serviceClass.name, address, serviceClass.name, false)
        var helsesjekk: Helsesjekk = object : Helsesjekk {
            override fun helsesjekk() {
                ping(pingPort)
            }

            override fun getMetadata(): HelsesjekkMetadata {
                return helsesjekkMetadata
            }
        }
        return PortType<T>().apply {
            port = portType
            helsesjekk = helsesjekk
        }

    }

    private fun <T> clientBuilder(address: String, serviceClass: Class<T>, wsdlUrl: String): CXFClient<T> {
        return CXFClient(serviceClass)
                .address(address)
                .wsdl(wsdlUrl)
                .timeout(5000, 20000)
                .withProperty("jaxb.additionalContextClasses", arrayOf(
                        XMLHenvendelse::class.java,
                        XMLMetadataListe::class.java,
                        XMLMeldingFraBruker::class.java,
                        XMLMeldingTilBruker::class.java
                ))
    }

    @Builder
    class PortType<T> {
        var port: T? = null
        var helsesjekk: Helsesjekk? = null
    }
}
