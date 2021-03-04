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
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangDTO
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService
import no.nav.sbl.dialogarena.mininnboks.dummyPrincipalNiva4
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse
import org.apache.commons.lang3.StringUtils.join
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.*
import org.hamcrest.core.Is
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*
import javax.xml.namespace.QName
import javax.xml.soap.SOAPFactory
import javax.xml.ws.soap.SOAPFaultException

class HenvendelseControllerTest : Spek({

    describe("Henvendelse tester") {

        val service = mockk<HenvendelseService>(relaxed = true)
        val tilgangService = mockk<TilgangService>()
        val mapper = jacksonObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        beforeEachTest {

            setUp(service)
        }

        val engine = TestApplicationEngine(createTestEnvironment())
        engine.start(wait = false) // for now we can't eliminate it

        engine.application.routing {
            authenticateWithDummySubject(dummyPrincipalNiva4()) {
                henvendelseController(service, tilgangService)
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

            it("filtrerer Bort Uavsluttede Delsvar") {
                val lstHenvendelserBehandlingskjedeMedDelsvar = listOf(
                    Henvendelse(id = "123", traadId = "1", type = Henvendelsetype.SPORSMAL_SKRIFTLIG, opprettet = TestUtils.now()),
                    Henvendelse(id = "234", traadId = "1", type = Henvendelsetype.DELVIS_SVAR_SKRIFTLIG, opprettet = TestUtils.now())
                )
                coEvery { service.hentAlleHenvendelser(any()) } returns lstHenvendelserBehandlingskjedeMedDelsvar
                handleRequest(HttpMethod.Get, "/traader") {
                }.apply {
                    MatcherAssert.assertThat(
                        response.content, not(stringContainsInOrder(Henvendelsetype.DELVIS_SVAR_SKRIFTLIG.name))
                    )
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
                coEvery { service.hentTraad(any(), any()) } throws SOAPFaultException(SOAPFactory.newInstance().createFault("Error", QName.valueOf("")))

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

            it("kan Ikke Sende Svar Nar Siste Henvendelse Ikke Er Sporsmal") {
                handleRequest(HttpMethod.Post, "/traader/svar") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")
                    setBody(mapper.writeValueAsString(Svar("1", "Tekst")))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.NotAcceptable.value))
                }
            }

            it("kan Sende Svar Nar Siste Henvendelse Er Sporsmal") {

                handleRequest(HttpMethod.Post, "/traader/svar") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")

                    setBody(mapper.writeValueAsString(Svar("2", "Tekst")))
                }.apply {

                    val nyHenvendelseResultat: NyHenvendelseResultat? = response.content?.let { mapper.readValue(it) }
                    MatcherAssert.assertThat(nyHenvendelseResultat?.behandlingsId, Is.`is`(CoreMatchers.not(CoreMatchers.nullValue())))
                }
            }

            it("kopierer Nyeste Er Tilknyttet Ansatt Flagg Til Svaret") {

                val henvendelse1 = Henvendelse(id = "1", erTilknyttetAnsatt = true, opprettet = TestUtils.now(), type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE)
                val henvendelse2 = Henvendelse(id = "2", erTilknyttetAnsatt = false, opprettet = TestUtils.now(), type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE)
                val henvendelser = listOf(henvendelse1, henvendelse2)

                coEvery { service.hentTraad(any(), any()) } returns (henvendelser)

                handleRequest(HttpMethod.Post, "/traader/svar") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")

                    setBody(mapper.writeValueAsString(Svar("0", "fritekst")))
                }.apply {
                    val henvendelseArgumentCaptor = slot<Henvendelse>()
                    coVerify { service.sendSvar(capture(henvendelseArgumentCaptor), any()) }
                    MatcherAssert.assertThat(henvendelseArgumentCaptor.captured.erTilknyttetAnsatt, Is.`is`(true))
                }
            }

            it("kopierer Brukers Enhet Til Svaret") {
                val brukersEnhet = "1234"
                val henvendelse1 = Henvendelse(id = "1", brukersEnhet = brukersEnhet, opprettet = TestUtils.nowPlus(-1), type = Henvendelsetype.SPORSMAL_SKRIFTLIG)
                val henvendelse2 = Henvendelse(id = "2", opprettet = TestUtils.now(), type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE)

                coEvery { service.hentTraad(any(), any()) } returns (listOf(henvendelse1, henvendelse2))
                handleRequest(HttpMethod.Post, "/traader/svar") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")

                    setBody(mapper.writeValueAsString(Svar("0", "fritekst")))
                }.apply {
                    val henvendelseArgumentCaptor = slot<Henvendelse>()
                    coVerify { service.sendSvar(capture(henvendelseArgumentCaptor), any()) }

                    MatcherAssert.assertThat(henvendelseArgumentCaptor.captured.brukersEnhet, Is.`is`(brukersEnhet))
                }
            }

            it("sender Ikke Funnet Naar Traad Optional Ikke Er Present") {
                coEvery { service.hentTraad(any(), any()) } returns emptyList()
                handleRequest(HttpMethod.Post, "/traader/svar") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")

                    setBody(mapper.writeValueAsString(Svar("0", "fritekst")))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.NotFound.value))
                }
            }

            it("smeller Hvis Tom FritekstI Sporsmal") {
                handleRequest(HttpMethod.Post, "/traader/sporsmal") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")

                    val sporsmal = Sporsmal(Temagruppe.ARBD, "")
                    setBody(mapper.writeValueAsString(sporsmal))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.BadRequest.value))
                }
            }

            it("smeller Hvis For Lang Fritekst I Sporsmal") {
                handleRequest(HttpMethod.Post, "/traader/sporsmal") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")
                    val sporsmal = Sporsmal(Temagruppe.ARBD, join(Collections.nCopies(1001, 'a'), ""))
                    setBody(mapper.writeValueAsString(sporsmal))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.BadRequest.value))
                }
            }

            it("smeller Hvis Andre Sosial tjenester Temagruppe I Sporsmal") {
                handleRequest(HttpMethod.Post, "/traader/sporsmal") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")
                    setBody(mapper.writeValueAsString(createSporsmal()))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.BadRequest.value))
                }
            }

            it("smeller Hvis Bruker Er Kode6 Og Temagruppe OKSOS") {
                coEvery {
                    tilgangService.harTilgangTilKommunalInnsending(any())
                } returns
                    TilgangDTO(TilgangDTO.Resultat.KODE6, "melding")
                handleRequest(HttpMethod.Post, "/traader/sporsmal") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")
                    setBody(mapper.writeValueAsString(createSporsmal()))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.BadRequest.value))
                }
            }

            it("smeller Hvis Bruker Ikke Har Enhet Og Temagruppe OKSOS") {
                coEvery {
                    tilgangService.harTilgangTilKommunalInnsending(any())
                } returns (
                    TilgangDTO(TilgangDTO.Resultat.INGEN_ENHET, "melding")
                    )
                handleRequest(HttpMethod.Post, "/traader/sporsmal") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")
                    setBody(mapper.writeValueAsString(createSporsmal()))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.BadRequest.value))
                }
            }

            it("smeller Hvis Temagruppe OKSOS Og Utledning Feiler") {
                coEvery {
                    tilgangService.harTilgangTilKommunalInnsending(any())
                } returns (
                    TilgangDTO(TilgangDTO.Resultat.FEILET, "melding")
                    )
                handleRequest(HttpMethod.Post, "/traader/sporsmal") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")
                    setBody(mapper.writeValueAsString(createSporsmal()))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.BadRequest.value))
                }
            }
        }
    }
})

private fun createSporsmal() = Sporsmal(Temagruppe.ANSOS, "DUMMY")

fun setUp(service: HenvendelseService) {
    val henvendelser = listOf(
        Henvendelse(id = "1", traadId = "1", type = Henvendelsetype.SAMTALEREFERAT_OPPMOTE, opprettet = TestUtils.now()),
        Henvendelse(id = "2", traadId = "2", type = Henvendelsetype.SAMTALEREFERAT_OPPMOTE, opprettet = TestUtils.now()),
        Henvendelse(id = "3", traadId = "1", type = Henvendelsetype.SAMTALEREFERAT_OPPMOTE, opprettet = TestUtils.now()),
        Henvendelse(id = "4", traadId = "3", type = Henvendelsetype.SAMTALEREFERAT_OPPMOTE, opprettet = TestUtils.now()),
        Henvendelse(id = "5", traadId = "1", type = Henvendelsetype.SAMTALEREFERAT_OPPMOTE, opprettet = TestUtils.now()),
        Henvendelse(id = "6", traadId = "2", type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE, opprettet = TestUtils.nowPlus(100)),
        Henvendelse(id = "7", traadId = "1", type = Henvendelsetype.SAMTALEREFERAT_OPPMOTE, opprettet = TestUtils.now())
    )

    val slot = slot<String>()
    coEvery { service.hentAlleHenvendelser(any()) } returns henvendelser
    coEvery { service.hentTraad(capture(slot), any()) } answers {
        val traadId = slot.captured
        henvendelser
            .filter { henvendelse: Henvendelse? -> traadId == henvendelse!!.traadId }
    }

    coEvery { service.sendSvar(any(), any()) } returns (
        WSSendInnHenvendelseResponse().withBehandlingsId(UUID.randomUUID().toString())
        )
}
