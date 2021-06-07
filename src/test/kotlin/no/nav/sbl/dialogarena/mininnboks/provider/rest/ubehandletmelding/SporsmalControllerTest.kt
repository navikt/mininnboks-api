package no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import no.nav.sbl.dialogarena.mininnboks.JacksonUtils.Companion.objectMapper
import no.nav.sbl.dialogarena.mininnboks.authenticateWithDummySubject
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.dummyPrincipalNiva3
import no.nav.sbl.dialogarena.mininnboks.dummySubject
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

class SporsmalControllerTest : Spek({
    val henvendelseService = mockk<HenvendelseService>()

    beforeEachTest {
        coEvery { (henvendelseService.hentAlleHenvendelser(dummySubject)) } returns emptyList()
    }

    describe("kaller Henvendelse Service Med SubjectID") {
        val engine = TestApplicationEngine(createTestEnvironment())
        engine.start(wait = false) // for now we can't eliminate it

        engine.application.routing {
            authenticateWithDummySubject(dummyPrincipalNiva3()) {
                sporsmalController(henvendelseService)
            }
        }
        engine.application.install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(objectMapper))
        }

        with(engine) {

            it("retunerer Exception") {
                handleRequest(HttpMethod.Get, "/sporsmal/ubehandlet") {
                }.apply {
                    assertThat(response.status(), Matchers.`is`(HttpStatusCode.OK))
                    coVerify(exactly = 1) { henvendelseService.hentAlleHenvendelser(any()) }
                }
            }
        }
    }
})
