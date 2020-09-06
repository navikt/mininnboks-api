package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.JacksonConverter
import io.ktor.routing.routing
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import io.ktor.server.testing.handleRequest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import no.nav.sbl.dialogarena.mininnboks.ObjectMapperProvider.Companion.objectMapper
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe


class SporsmalControllerTest : Spek({

    describe("kaller Henvendelse Service Med SubjectID") {
        val engine = TestApplicationEngine(createTestEnvironment() )
        engine.start(wait = false) // for now we can't eliminate it
        val henvendelseService = mockk<HenvendelseService>()
        engine.application.routing { sporsmalController(henvendelseService, false) }
        engine.application. install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(objectMapper))
        }

        with(engine) {
            every { (henvendelseService.hentAlleHenvendelser(any())) } returns emptyList<Henvendelse>()

            it("retunerer Exception") {

                handleRequest(HttpMethod.Get, "/sporsmal/ubehandlet") {

                }.apply {
                    assertEquals(HttpStatusCode.OK, response.status())
                    verify(exactly = 1) { henvendelseService.hentAlleHenvendelser(any()) }
                }
            }
        }
    }
})
