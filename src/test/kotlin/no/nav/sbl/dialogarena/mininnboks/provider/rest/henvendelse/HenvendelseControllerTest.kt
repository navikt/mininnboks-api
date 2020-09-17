package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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
import io.ktor.server.testing.setBody
import io.mockk.*
import io.mockk.every
import no.nav.common.auth.subject.Subject
import no.nav.sbl.dialogarena.mininnboks.ObjectMapperProvider
import no.nav.sbl.dialogarena.mininnboks.TestUtils
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangDTO
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse
import org.apache.commons.lang3.StringUtils.join
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.*
import org.hamcrest.core.Is
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*
import java.util.stream.Collectors
import javax.xml.namespace.QName
import javax.xml.soap.SOAPFactory
import javax.xml.ws.soap.SOAPFaultException


class HenvendelseControllerTest : Spek({

    describe("Henvendelse tester") {

        val service = mockk<HenvendelseService>(relaxed = true)
        val tilgangService = mockk<TilgangService>()
        val subject = mockk<Subject>()
        val mapper = jacksonObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        beforeEachTest {

            setUp(service, tilgangService)
        }

        val engine = TestApplicationEngine(createTestEnvironment())
        engine.start(wait = false) // for now we can't eliminate it

        engine.application.routing { henvendelseController(service, tilgangService, false) }
        engine.application.install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(ObjectMapperProvider.objectMapper))
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
                        Henvendelse("123").withType(Henvendelsetype.SPORSMAL_SKRIFTLIG).withTraadId("1").withOpprettetTid(TestUtils.now()),
                        Henvendelse("234").withType(Henvendelsetype.DELVIS_SVAR_SKRIFTLIG).withTraadId("1").withOpprettetTid(TestUtils.now())
                )
                coEvery { service.hentAlleHenvendelser(any()) } returns lstHenvendelserBehandlingskjedeMedDelsvar
                handleRequest(HttpMethod.Get, "/traader") {
                }.apply {
                    MatcherAssert.assertThat(response.content, not(stringContainsInOrder(Henvendelsetype.DELVIS_SVAR_SKRIFTLIG.name))
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
                    coVerify (exactly = 1) { service.merkSomLest( "1", any()) }
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
                    val svar = Svar()
                    svar.traadId = "1"
                    svar.fritekst = "Tekst"
                    setBody(mapper.writeValueAsString(svar))

                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.NotAcceptable.value))
                }
            }

            it("kan Sende Svar Nar Siste Henvendelse Er Sporsmal") {

                handleRequest(HttpMethod.Post, "/traader/svar") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")

                    val svar = Svar()
                    svar.traadId = "2"
                    svar.fritekst = "Tekst"
                    setBody(mapper.writeValueAsString(svar))
                }.apply {

                    val nyHenvendelseResultat: NyHenvendelseResultat? = response.content?.let { mapper.readValue(it) }
                    MatcherAssert.assertThat(nyHenvendelseResultat?.behandlingsId, Is.`is`(CoreMatchers.not(CoreMatchers.nullValue())))
                }
            }


            it("kopierer Nyeste Er Tilknyttet Ansatt Flagg Til Svaret") {

                val henvendelse1 = Henvendelse("1")
                henvendelse1.erTilknyttetAnsatt = true
                henvendelse1.opprettet = TestUtils.now()
                henvendelse1.type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE
                val henvendelse2 = Henvendelse("2")
                henvendelse2.erTilknyttetAnsatt = false
                henvendelse2.opprettet = TestUtils.now()
                val henvendelser = listOf(henvendelse1, henvendelse2)

                coEvery { service.hentTraad(any(), any()) } returns (henvendelser)

                handleRequest(HttpMethod.Post, "/traader/svar") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")


                    val svar = Svar()
                    svar.fritekst = "fritekst"
                    svar.traadId = "0"
                    setBody(mapper.writeValueAsString(svar))
                }.apply {
                    val henvendelseArgumentCaptor = slot<Henvendelse>()
                    coVerify { service.sendSvar(capture(henvendelseArgumentCaptor), any()) }
                    MatcherAssert.assertThat(henvendelseArgumentCaptor.captured.erTilknyttetAnsatt, Is.`is`(true))
                }

            }


            it("kopierer Brukers Enhet Til Svaret") {
                val brukersEnhet = "1234"
                val henvendelse1 = Henvendelse("1")
                henvendelse1.opprettet = TestUtils.nowPlus(-1)
                henvendelse1.type = Henvendelsetype.SPORSMAL_SKRIFTLIG
                henvendelse1.brukersEnhet = brukersEnhet
                val henvendelse2 = Henvendelse("2")
                henvendelse2.opprettet = TestUtils.now()
                henvendelse2.type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE
                coEvery { service.hentTraad(any(), any()) } returns (listOf(henvendelse1, henvendelse2))
                handleRequest(HttpMethod.Post, "/traader/svar") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")
                    val svar = Svar()
                    svar.fritekst = "fritekst"
                    svar.traadId = "0"
                    setBody(mapper.writeValueAsString(svar))
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
                    val svar = Svar()
                    svar.fritekst = "fritekst"
                    svar.traadId = "0"
                    setBody(mapper.writeValueAsString(svar))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.NotFound.value))
                }
            }

            it("smeller Hvis Tom FritekstI Sporsmal") {
                handleRequest(HttpMethod.Post, "/traader/sporsmal") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")

                    val sporsmal = Sporsmal()
                    sporsmal.fritekst = ""
                    sporsmal.temagruppe = Temagruppe.ARBD.name
                    setBody(mapper.writeValueAsString(sporsmal))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.BadRequest.value))
                }
            }

            it("smeller Hvis For Lang Fritekst I Sporsmal") {
                handleRequest(HttpMethod.Post, "/traader/sporsmal") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")
                    val sporsmal = Sporsmal()
                    sporsmal.fritekst = join(Collections.nCopies(1001, 'a'), "")
                    sporsmal.temagruppe = Temagruppe.ARBD.name
                    setBody(mapper.writeValueAsString(sporsmal))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.BadRequest.value))
                }
            }


            it("smeller Hvis Andre Sosial tjenester Temagruppe I Sporsmal") {
                handleRequest(HttpMethod.Post, "/traader/sporsmal") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")
                    val sporsmal = Sporsmal()
                    sporsmal.fritekst = "DUMMY"
                    sporsmal.temagruppe = Temagruppe.ANSOS.name
                    setBody(mapper.writeValueAsString(sporsmal))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.BadRequest.value))
                }
            }

            it("smeller Hvis Bruker Er Kode6 Og Temagruppe OKSOS") {
                every {
                    tilgangService.harTilgangTilKommunalInnsending(any())
                } returns
                        TilgangDTO(TilgangDTO.Resultat.KODE6, "melding")
                handleRequest(HttpMethod.Post, "/traader/sporsmal") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")
                    val sporsmal = Sporsmal()
                    sporsmal.fritekst = "DUMMY"
                    sporsmal.temagruppe = Temagruppe.OKSOS.name
                    setBody(mapper.writeValueAsString(sporsmal))
                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.BadRequest.value))
                }
            }

            it("smeller Hvis Bruker Ikke Har Enhet Og Temagruppe OKSOS") {
                every {
                    tilgangService.harTilgangTilKommunalInnsending(any())
                } returns (
                        TilgangDTO(TilgangDTO.Resultat.INGEN_ENHET, "melding")
                        )
                handleRequest(HttpMethod.Post, "/traader/sporsmal") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")
                    val sporsmal = Sporsmal()
                    sporsmal.fritekst = "DUMMY"
                    sporsmal.temagruppe = Temagruppe.OKSOS.name
                    setBody(mapper.writeValueAsString(sporsmal))

                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.BadRequest.value))
                }
            }

            it("smeller Hvis Temagruppe OKSOS Og Utledning Feiler") {
                every {
                    tilgangService.harTilgangTilKommunalInnsending(any())
                } returns (
                        TilgangDTO(TilgangDTO.Resultat.FEILET, "melding")
                        )
                handleRequest(HttpMethod.Post, "/traader/sporsmal") {

                    addHeader("Content-Type", "application/json; charset=utf8")
                    addHeader("Accept", "application/json")
                    val sporsmal = Sporsmal()
                    sporsmal.fritekst = "DUMMY"
                    sporsmal.temagruppe = Temagruppe.OKSOS.name
                    setBody(mapper.writeValueAsString(sporsmal))

                }.apply {
                    MatcherAssert.assertThat(response.status()?.value, Is.`is`(HttpStatusCode.BadRequest.value))
                }
            }
        }
    }

})


fun setUp(service: HenvendelseService, tilgangService: TilgangService) {
    val henvendelser = listOf(
            Henvendelse("1").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
            Henvendelse("2").withTraadId("2").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
            Henvendelse("3").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
            Henvendelse("4").withTraadId("3").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
            Henvendelse("5").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
            Henvendelse("6").withTraadId("2").withType(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE).withOpprettetTid(TestUtils.nowPlus(100)),
            Henvendelse("7").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now())
    )

    val slot = slot<String>()
    coEvery { service.hentAlleHenvendelser(any()) } returns henvendelser
    coEvery { service.hentTraad(capture(slot), any()) } answers {
        val traadId = slot.captured
        henvendelser.stream()
                .filter { henvendelse: Henvendelse? -> traadId == henvendelse!!.traadId }
                .collect(Collectors.toList())
    }

    coEvery { service.sendSvar(any(), any()) } returns (
            WSSendInnHenvendelseResponse().withBehandlingsId(UUID.randomUUID().toString())
            )
}


