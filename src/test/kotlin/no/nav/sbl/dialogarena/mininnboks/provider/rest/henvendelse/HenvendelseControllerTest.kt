package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import no.nav.sbl.dialogarena.mininnboks.JacksonUtils
import no.nav.sbl.dialogarena.mininnboks.TestUtils
import no.nav.sbl.dialogarena.mininnboks.authenticateWithDummySubject
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Traad
import no.nav.sbl.dialogarena.mininnboks.dummyPrincipalNiva4
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.Is
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import javax.xml.namespace.QName
import javax.xml.soap.SOAPFactory
import javax.xml.ws.soap.SOAPFaultException

private val service = mockk<HenvendelseService>(relaxed = true)
private val mapper = jacksonObjectMapper()

object HenvendelseControllerTest : Spek({

    describe("Henvendelse tester") {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        beforeEachTest {
            setUp(service)
        }

        val engine = TestApplicationEngine(createTestEnvironment())
        engine.start(wait = false) // for now we can't eliminate it

        engine.application.routing {
            authenticateWithDummySubject(dummyPrincipalNiva4()) {
                henvendelseController(service)
            }
        }
        engine.application.install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(JacksonUtils.objectMapper))
        }
        with(engine) {
            it("henter Ut Alle Henvendelser Og Gjor Om Til Traader") {
                handleRequest(HttpMethod.Get, "/traader") {
                }.apply {
                    val traader: List<Traad>? = response.content?.let { mapper.readValue(it) }
                    MatcherAssert.assertThat(traader?.size, `is`(3))
                }
            }

            it("henter Ut Enkelt Traad Basert PaId") {
                handleRequest(HttpMethod.Get, "/traader/1") {
                }.apply {
                    val traad1: Traad? = response.content?.let { mapper.readValue(it) }
                    MatcherAssert.assertThat(traad1?.meldinger?.size, `is`(4))
                }

                handleRequest(HttpMethod.Get, "/traader/2") {
                }.apply {
                    val traad2: Traad? = response.content?.let { mapper.readValue(it) }
                    MatcherAssert.assertThat(traad2?.meldinger?.size, Is.`is`(2))
                }

                handleRequest(HttpMethod.Get, "/traader/3") {
                }.apply {
                    val traad3: Traad? = response.content?.let { mapper.readValue(it) }
                    MatcherAssert.assertThat(traad3?.meldinger?.size, Is.`is`(1))
                }
            }

            it("henter Ut Traad Som Ikke Finnes") {
                handleRequest(HttpMethod.Get, "/traader/avabv") {
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(404))
                }
            }

            it("gir Statuskode Ikke Funnet Hvis Henvendelse Service Gir Soap Fault") {
                coEvery { service.hentTraad(any(), any()) } throws SOAPFaultException(
                    SOAPFactory.newInstance().createFault("Error", QName.valueOf(""))
                )

                handleRequest(HttpMethod.Get, "/traader/1") {
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, `is`(HttpStatusCode.NotFound.value))
                }
            }

            it("markering Som Lest") {
                handleRequest(HttpMethod.Post, "/traader/lest/1") {
                }.apply {
                    coVerify(exactly = 1) { service.merkSomLest("1", any()) }
                }
            }

            it("markering Alle Som Lest") {
                handleRequest(HttpMethod.Post, "/traader/allelest/1") {
                }.apply {
                    coVerify(exactly = 1) { service.merkAlleSomLest("1", any()) }
                }
            }
        }
    }
})

private fun setUp(service: HenvendelseService) {
    val henvendelser = listOf(
        Henvendelse(
            id = "1",
            traadId = "1",
            type = Henvendelsetype.SAMTALEREFERAT_OPPMOTE,
            opprettet = TestUtils.now()
        ),
        Henvendelse(
            id = "2",
            traadId = "2",
            type = Henvendelsetype.SAMTALEREFERAT_OPPMOTE,
            opprettet = TestUtils.now()
        ),
        Henvendelse(
            id = "3",
            traadId = "1",
            type = Henvendelsetype.SAMTALEREFERAT_OPPMOTE,
            opprettet = TestUtils.now()
        ),
        Henvendelse(
            id = "4",
            traadId = "3",
            type = Henvendelsetype.SAMTALEREFERAT_OPPMOTE,
            opprettet = TestUtils.now()
        ),
        Henvendelse(
            id = "5",
            traadId = "1",
            type = Henvendelsetype.SAMTALEREFERAT_OPPMOTE,
            opprettet = TestUtils.now()
        ),
        Henvendelse(
            id = "6",
            traadId = "2",
            type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE,
            opprettet = TestUtils.nowPlus(100)
        ),
        Henvendelse(id = "7", traadId = "1", type = Henvendelsetype.SAMTALEREFERAT_OPPMOTE, opprettet = TestUtils.now())
    )

    val slot = slot<String>()
    coEvery { service.hentAlleHenvendelser(any()) } returns henvendelser
    coEvery { service.hentTraad(capture(slot), any()) } answers {
        val traadId = slot.captured
        henvendelser
            .filter { henvendelse: Henvendelse? -> traadId == henvendelse!!.traadId }
    }
}
