package no.nav.sbl.dialogarena.mininnboks.consumer.utils

import java.io.IOException
import javax.ws.rs.client.ClientRequestContext
import javax.ws.rs.client.ClientRequestFilter

class ApigwRequestFilter(private val apikey: String) : ClientRequestFilter {
    @Throws(IOException::class)
    override fun filter(request: ClientRequestContext) {
        request.headers.putSingle("x-nav-apiKey", apikey)
    }

}
