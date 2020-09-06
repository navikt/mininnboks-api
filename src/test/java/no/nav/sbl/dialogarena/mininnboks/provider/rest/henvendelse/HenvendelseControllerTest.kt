package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
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
import io.mockk.slot
import io.mockk.verify
import junit.framework.Assert
import no.nav.sbl.dialogarena.mininnboks.ObjectMapperProvider
import no.nav.sbl.dialogarena.mininnboks.TestUtils
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangDTO
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.sporsmalController
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse
import org.apache.commons.lang3.StringUtils
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.stringContainsInOrder
import org.hamcrest.core.Is
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*
import java.util.stream.Collectors
import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response

class HenvendelseControllerTest : Spek({

    describe("Henvendelse tester") {

        val service = mockk<HenvendelseService>()
        val tilgangService  = mockk<TilgangService>()

        beforeEachTest {

            setUp(service, tilgangService)
        }

        val engine = TestApplicationEngine(createTestEnvironment())
        engine.start(wait = false) // for now we can't eliminate it

        engine.application.routing { HenvendelseController(service, tilgangService, false) }
        engine.application.install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(ObjectMapperProvider.objectMapper))
        }
        with(engine) {
          //  every { (henvendelseService.hentAlleHenvendelser(any())) } returns emptyList<Henvendelse>()

            it( "filtrerer Bort Uavsluttede Delsvar") {
                val lstHenvendelserBehandlingskjedeMedDelsvar = listOf(
                        Henvendelse("123").withType(Henvendelsetype.SPORSMAL_SKRIFTLIG).withTraadId("1").withOpprettetTid(TestUtils.now()),
                        Henvendelse("234").withType(Henvendelsetype.DELVIS_SVAR_SKRIFTLIG).withTraadId("1").withOpprettetTid(TestUtils.now())
                )
                every {service.hentAlleHenvendelser(any()) } returns lstHenvendelserBehandlingskjedeMedDelsvar
                handleRequest(HttpMethod.Get, "/traader") {
                }.apply {
                    MatcherAssert.assertThat(response.content, stringContainsInOrder(Henvendelsetype.DELVIS_SVAR_SKRIFTLIG.name))
                }

            }
        }
    }
})




   // @Rule
    //var subjectRule = SubjectRule(Subject("fnr", IdentType.EksternBruker, SsoToken.oidcToken("token", emptyMap<String, Any>())))

    fun setUp(service: HenvendelseService , tilgangService :TilgangService) {
        val henvendelser = Arrays.asList(
                Henvendelse("1").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
                Henvendelse("2").withTraadId("2").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
                Henvendelse("3").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
                Henvendelse("4").withTraadId("3").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
                Henvendelse("5").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
                Henvendelse("6").withTraadId("2").withType(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE).withOpprettetTid(TestUtils.nowPlus(100)),
                Henvendelse("7").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now())
        )

        val slot = slot<String>()
        every { service.hentAlleHenvendelser(any()) } returns henvendelser
        every { service.hentTraad(capture(slot)) } answers {
            val traadId = slot.captured
            henvendelser.stream()
                    .filter { henvendelse: Henvendelse? -> traadId == henvendelse!!.traadId }
                    .collect(Collectors.toList())
        }
        every {service.sendSvar(any(), any())} returns (
                WSSendInnHenvendelseResponse().withBehandlingsId(UUID.randomUUID().toString())
        )
    }

/*
    @Test
    @Throws(Exception::class)


    @Test
    @Throws(Exception::class)
   it("henter Ut Enkelt Traad Basert PaId") {
        val traad1 = controller.hentEnkeltTraad("1").entity as Traad
        val traad2 = controller.hentEnkeltTraad("2").entity as Traad
        val traad3 = controller.hentEnkeltTraad("3").entity as Traad
        MatcherAssert.assertThat(traad1.meldinger.size, Is.`is`(4))
        MatcherAssert.assertThat(traad2.meldinger.size, Is.`is`(2))
        MatcherAssert.assertThat(traad3.meldinger.size, Is.`is`(1))
    }

    @Test
    @Throws(Exception::class)
   it("henter Ut Traad Som Ikke Finnes") {
        val response = controller.hentEnkeltTraad("avabv")
        MatcherAssert.assertThat(response.status, Is.`is`(404))
    }

    @Test
   it("gir Statuskode Ikke Funnet Hvis Henvendelse Service Gir Soap Fault") {
        every {service!!.hentTraad(any()) } throws SOAPFaultException()
        val response = controller.hentEnkeltTraad("1")
        MatcherAssert.assertThat(response.status, Is.`is`(Response.Status.NOT_FOUND.statusCode))
    }

    @Test
    @Throws(Exception::class)
   it("markering Som Lest") {
        controller.markerSomLest("1")
        Mockito.verify(service, Mockito.times(1))?.merkSomLest("1")
    }

    @Test
    @Throws(Exception::class)
   it("markering Alle Som Lest") {
        controller.markerAlleSomLest("1")
        Mockito.verify(service, Mockito.times(1))?.merkAlleSomLest("1")
    }

    @Test
    @Throws(Exception::class)
   it("kan Ikke Sende Svar Nar Siste Henvendelse Ikke Er Sporsmal") {
        val svar = Svar()
        svar.traadId = "1"
        svar.fritekst = "Tekst"
        val response = controller.sendSvar(svar)
        MatcherAssert.assertThat(response.status, Is."is"(Response.Status.NOT_ACCEPTABLE.statusCode))
    }

    @Test
    @Throws(Exception::class)
   it("kan Sende Svar Nar Siste Henvendelse Er Sporsmal") {
        val svar = Svar()
        svar.traadId = "2"
        svar.fritekst = "Tekst"
        val nyHenvendelseResultat = controller.sendSvar(svar).entity as NyHenvendelseResultat
        MatcherAssert.assertThat(nyHenvendelseResultat.behandlingsId, Is.`is`(CoreMatchers.not(CoreMatchers.nullValue())))
    }

    @Test
   it("kopierer Nyeste Er Tilknyttet Ansatt Flagg Til Svaret") {
        val henvendelse1 = Henvendelse("1")
        henvendelse1.erTilknyttetAnsatt = true
        henvendelse1.opprettet = TestUtils.now()
        henvendelse1.type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE
        val henvendelse2 = Henvendelse("2")
        henvendelse2.erTilknyttetAnsatt = false
        henvendelse2.opprettet = TestUtils.now()
        val henvendelser = Arrays.asList(henvendelse1, henvendelse2)
        every {service!!.hentTraad(ArgumentMatchers.anyString())} returns (henvendelser)
        val svar = Svar()
        svar.fritekst = "fritekst"
        svar.traadId = "0"
        controller.sendSvar(svar)
        val henvendelseArgumentCaptor = ArgumentCaptor.forClass(Henvendelse::class.java)
        Mockito.verify(service)?.sendSvar(henvendelseArgumentCaptor.capture(), ArgumentMatchers.anyString())
        MatcherAssert.assertThat(henvendelseArgumentCaptor.value.erTilknyttetAnsatt, Is."is"(true))
    }

    @Test
   it("kopierer Brukers Enhet Til Svaret") {
        val brukersEnhet = "1234"
        val henvendelse1 = Henvendelse("1")
        henvendelse1.opprettet = TestUtils.nowPlus(-1)
        henvendelse1.type = Henvendelsetype.SPORSMAL_SKRIFTLIG
        henvendelse1.brukersEnhet = brukersEnhet
        val henvendelse2 = Henvendelse("2")
        henvendelse2.opprettet = TestUtils.now()
        henvendelse2.type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE
        every {service!!.hentTraad(anyString())} returns (Arrays.asList(henvendelse1, henvendelse2))
        val svar = Svar()
        svar.fritekst = "fritekst"
        svar.traadId = "0"
        controller.sendSvar(svar)
        val henvendelseArgumentCaptor = ArgumentCaptor.forClass(Henvendelse::class.java)
        Mockito.verify(service)?.sendSvar(henvendelseArgumentCaptor.capture(), ArgumentMatchers.anyString())
        MatcherAssert.assertThat(henvendelseArgumentCaptor.value.brukersEnhet, Is.`is`(brukersEnhet))
    }

    @Test
   it("sender Ikke Funnet Naar Traad Optional Ikke Er Present") {
        every {service!!.hentTraad(anyString())} returns (null)
        val svar = Svar()
        svar.fritekst = "fritekst"
        svar.traadId = "0"
        val response = controller.sendSvar(svar)
        MatcherAssert.assertThat(response.status, Is.`is`(Response.Status.NOT_FOUND.statusCode))
    }

    @Test(expected = BadRequestException::class)
   it("smeller Hvis Tom FritekstI Sporsmal") {
        val sporsmal = Sporsmal()
        sporsmal.fritekst = ""
        sporsmal.temagruppe = Temagruppe.ARBD.name
        controller.sendSporsmal(sporsmal)
    }

    @Test(expected = BadRequestException::class)
   it(`smeller Hvis For Lang Fritekst I Sporsmal") {
        val sporsmal = Sporsmal()
        sporsmal.fritekst = StringUtils.join(Collections.nCopies(1001, 'a'), "")
        sporsmal.temagruppe = Temagruppe.ARBD.name
        controller.sendSporsmal(sporsmal)
    }

    @Test(expected = BadRequestException::class)
   it("smeller Hvis Andre Sosial tjenester Temagruppe I Sporsmal") {
        val sporsmal = Sporsmal()
        sporsmal.fritekst = "DUMMY"
        sporsmal.temagruppe = Temagruppe.ANSOS.name
        controller.sendSporsmal(sporsmal)
    }

    @Test(expected = BadRequestException::class)
   it("smeller Hvis Bruker Er Kode6 Og Temagruppe OKSOS") {
        every {tilgangService.harTilgangTilKommunalInnsending(ArgumentMatchers.anyString())).thenReturn(
                TilgangDTO(TilgangDTO.Resultat.KODE6, "melding")
        )
        val sporsmal = Sporsmal()
        sporsmal.fritekst = "DUMMY"
        sporsmal.temagruppe = Temagruppe.OKSOS.name
        controller.sendSporsmal(sporsmal)
    }

    @Test(expected = BadRequestException::class)
   it("smeller Hvis Bruker Ikke Har Enhet Og Temagruppe OKSOS") {
        every {tilgangService.harTilgangTilKommunalInnsending(ArgumentMatchers.anyString())).thenReturn(
                TilgangDTO(TilgangDTO.Resultat.INGEN_ENHET, "melding")
        )
        val sporsmal = Sporsmal()
        sporsmal.fritekst = "DUMMY"
        sporsmal.temagruppe = Temagruppe.OKSOS.name
        controller.sendSporsmal(sporsmal)
    }

    @Test(expected = BadRequestException::class)
   it("smeller Hvis Temagruppe OKSOS Og Utledning Feiler") {
        every {tilgangService.harTilgangTilKommunalInnsending(ArgumentMatchers.anyString())).thenReturn(
                TilgangDTO(TilgangDTO.Resultat.FEILET, "melding")
        )
        val sporsmal = Sporsmal()
        sporsmal.fritekst = "DUMMY"
        sporsmal.temagruppe = Temagruppe.OKSOS.name
        controller.sendSporsmal(sporsmal)
    }

})*/

