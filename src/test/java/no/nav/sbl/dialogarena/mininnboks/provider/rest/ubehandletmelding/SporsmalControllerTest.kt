package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import com.auth0.jwt.JWT
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.routing.routing
import io.mockk.mockk
import io.mockk.verify
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import org.mockito.ArgumentMatchers
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication.Feature.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.*
import io.ktor.server.testing.*
import io.mockk.every
import no.nav.sbl.dialogarena.mininnboks.ObjectMapperProvider.Companion.objectMapper
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.mininboks


class SporsmalControllerTest: Spek({

    describe("kaller Henvendelse Service Med SubjectID"){
        withTestApplication({mininboks(true)}) {
            val henvendelseService = mockk<HenvendelseService>()
            application.routing { sporsmalController(henvendelseService, false) }
            every { (henvendelseService.hentAlleHenvendelser(any())) } returns listOf(Henvendelse("223"))

            it("retunerer Exception") {

                with(handleRequest(HttpMethod.Get, "/sporsmal/ubehandlet") {
                    addHeader(HttpHeaders.ContentType, ContentType.Application.Json.contentType)

                }) {
                    verify(exactly=1) {henvendelseService.hentAlleHenvendelser(any())}

                }
        }

        }
    }
})
