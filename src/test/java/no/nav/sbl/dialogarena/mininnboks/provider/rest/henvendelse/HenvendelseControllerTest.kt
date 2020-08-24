package no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import no.nav.brukerdialog.security.context.SubjectRule
import no.nav.brukerdialog.security.domain.IdentType
import no.nav.common.auth.SsoToken
import no.nav.common.auth.Subject
import no.nav.sbl.dialogarena.mininnboks.TestUtils
import no.nav.sbl.dialogarena.mininnboks.consumer.HenvendelseService
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstService
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangDTO
import no.nav.sbl.dialogarena.mininnboks.consumer.tilgang.TilgangService
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse
import org.apache.commons.lang3.StringUtils
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.*
import org.mockito.*
import org.mockito.invocation.InvocationOnMock
import org.mockito.junit.MockitoJUnit
import org.mockito.stubbing.Answer
import java.util.*
import java.util.stream.Collectors
import javax.ws.rs.BadRequestException
import javax.ws.rs.core.Response
import javax.xml.ws.soap.SOAPFaultException
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.mockito.Mockito.mock
import kotlin.test.*

class HenvendelseControllerTest {
    @Mock
    var service: HenvendelseService? =  mock()

    @Mock
    var tilgangService: TilgangService = mock()

   // @InjectMocks
   // var controller: Henvend= mock()

    @Rule
    var subjectRule = SubjectRule(Subject("fnr", IdentType.EksternBruker, SsoToken.oidcToken("token", emptyMap<String, Any>())))

    @Rule
    var rule = MockitoJUnit.rule().silent()

    @Before
    fun setup() {
        val henvendelser = Arrays.asList(
                Henvendelse("1").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
                Henvendelse("2").withTraadId("2").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
                Henvendelse("3").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
                Henvendelse("4").withTraadId("3").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
                Henvendelse("5").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now()),
                Henvendelse("6").withTraadId("2").withType(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE).withOpprettetTid(TestUtils.nowPlus(100)),
                Henvendelse("7").withTraadId("1").withType(Henvendelsetype.SAMTALEREFERAT_OPPMOTE).withOpprettetTid(TestUtils.now())
        )
        Mockito.`when`(service!!.hentAlleHenvendelser(ArgumentMatchers.anyString())).thenReturn(henvendelser)
        Mockito.`when`(service!!.hentTraad(ArgumentMatchers.anyString())).thenAnswer(Answer { invocation: InvocationOnMock ->
            val traadId = invocation.arguments[0] as String
            henvendelser.stream()
                    .filter { henvendelse: Henvendelse? -> traadId == henvendelse!!.traadId }
                    .collect(Collectors.toList())
        } as Answer<List<Henvendelse>>)
        Mockito.`when`(service!!.sendSvar(ArgumentMatchers.any(Henvendelse::class.java), ArgumentMatchers.anyString())).thenReturn(
                WSSendInnHenvendelseResponse().withBehandlingsId(UUID.randomUUID().toString())
        )
    }


    @Test
    @Throws(Exception::class)
    fun henterUtAlleHenvendelserOgGjorOmTilTraader() {
        val traader = service.hentAlleHenvendelser(any())
        MatcherAssert.assertThat(traader.size, Is.`is`(3))
    }

    @Test
    fun filtrererBortUavsluttedeDelsvar() {
        Mockito.`when`(service!!.hentAlleHenvendelser(ArgumentMatchers.anyString())).thenReturn(mockBehandlingskjedeMedDelsvar())
        val traader = controller.hentTraader()
        val delsvar = traader[0].meldinger.stream()
                .filter { henvendelse: Henvendelse -> henvendelse.type == Henvendelsetype.DELVIS_SVAR_SKRIFTLIG }
                .findAny()
        MatcherAssert.assertThat(delsvar.isPresent, Is.`is`(false))
    }

    private fun mockBehandlingskjedeMedDelsvar(): List<Henvendelse?> {
        return Arrays.asList(
                Henvendelse("123").withType(Henvendelsetype.SPORSMAL_SKRIFTLIG).withTraadId("1").withOpprettetTid(TestUtils.now()),
                Henvendelse("234").withType(Henvendelsetype.DELVIS_SVAR_SKRIFTLIG).withTraadId("1").withOpprettetTid(TestUtils.now())
        )
    }

    @Test
    @Throws(Exception::class)
    fun serviceKanFeileUtenAtEndepunktFeiler() {
        Mockito.`when`(service!!.hentAlleHenvendelser(ArgumentMatchers.anyString())).thenReturn(emptyList())
        val traader = controller.hentTraader()
        MatcherAssert.assertThat(traader.size, Is.`is`(0))
    }

    @Test
    @Throws(Exception::class)
    fun henterUtEnkeltTraadBasertPaId() {
        val traad1 = controller.hentEnkeltTraad("1").entity as Traad
        val traad2 = controller.hentEnkeltTraad("2").entity as Traad
        val traad3 = controller.hentEnkeltTraad("3").entity as Traad
        MatcherAssert.assertThat(traad1.meldinger.size, Is.`is`(4))
        MatcherAssert.assertThat(traad2.meldinger.size, Is.`is`(2))
        MatcherAssert.assertThat(traad3.meldinger.size, Is.`is`(1))
    }

    @Test
    @Throws(Exception::class)
    fun henterUtTraadSomIkkeFinnes() {
        val response = controller.hentEnkeltTraad("avabv")
        MatcherAssert.assertThat(response.status, Is.`is`(404))
    }

    @Test
    fun girStatuskodeIkkeFunnetHvisHenvendelseServiceGirSoapFault() {
        Mockito.`when`(service!!.hentTraad(ArgumentMatchers.anyString())).thenThrow(SOAPFaultException::class.java)
        val response = controller.hentEnkeltTraad("1")
        MatcherAssert.assertThat(response.status, Is.`is`(Response.Status.NOT_FOUND.statusCode))
    }

    @Test
    @Throws(Exception::class)
    fun markeringSomLest() {
        controller.markerSomLest("1")
        Mockito.verify(service, Mockito.times(1))?.merkSomLest("1")
    }

    @Test
    @Throws(Exception::class)
    fun markeringAlleSomLest() {
        controller.markerAlleSomLest("1")
        Mockito.verify(service, Mockito.times(1))?.merkAlleSomLest("1")
    }

    @Test
    @Throws(Exception::class)
    fun kanIkkeSendeSvarNarSisteHenvendelseIkkeErSporsmal() {
        val svar = Svar()
        svar.traadId = "1"
        svar.fritekst = "Tekst"
        val response = controller.sendSvar(svar)
        MatcherAssert.assertThat(response.status, Is.`is`(Response.Status.NOT_ACCEPTABLE.statusCode))
    }

    @Test
    @Throws(Exception::class)
    fun kanSendeSvarNarSisteHenvendelseErSporsmal() {
        val svar = Svar()
        svar.traadId = "2"
        svar.fritekst = "Tekst"
        val nyHenvendelseResultat = controller.sendSvar(svar).entity as NyHenvendelseResultat
        MatcherAssert.assertThat(nyHenvendelseResultat.behandlingsId, Is.`is`(CoreMatchers.not(CoreMatchers.nullValue())))
    }

    @Test
    fun kopiererNyesteErTilknyttetAnsattFlaggTilSvaret() {
        val henvendelse1 = Henvendelse("1")
        henvendelse1.erTilknyttetAnsatt = true
        henvendelse1.opprettet = TestUtils.now()
        henvendelse1.type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE
        val henvendelse2 = Henvendelse("2")
        henvendelse2.erTilknyttetAnsatt = false
        henvendelse2.opprettet = TestUtils.now()
        val henvendelser = Arrays.asList(henvendelse1, henvendelse2)
        Mockito.`when`(service!!.hentTraad(ArgumentMatchers.anyString())).thenReturn(henvendelser)
        val svar = Svar()
        svar.fritekst = "fritekst"
        svar.traadId = "0"
        controller.sendSvar(svar)
        val henvendelseArgumentCaptor = ArgumentCaptor.forClass(Henvendelse::class.java)
        Mockito.verify(service)?.sendSvar(henvendelseArgumentCaptor.capture(), ArgumentMatchers.anyString())
        MatcherAssert.assertThat(henvendelseArgumentCaptor.value.erTilknyttetAnsatt, Is.`is`(true))
    }

    @Test
    fun kopiererBrukersEnhetTilSvaret() {
        val brukersEnhet = "1234"
        val henvendelse1 = Henvendelse("1")
        henvendelse1.opprettet = TestUtils.nowPlus(-1)
        henvendelse1.type = Henvendelsetype.SPORSMAL_SKRIFTLIG
        henvendelse1.brukersEnhet = brukersEnhet
        val henvendelse2 = Henvendelse("2")
        henvendelse2.opprettet = TestUtils.now()
        henvendelse2.type = Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE
        Mockito.`when`(service!!.hentTraad(ArgumentMatchers.anyString())).thenReturn(Arrays.asList(henvendelse1, henvendelse2))
        val svar = Svar()
        svar.fritekst = "fritekst"
        svar.traadId = "0"
        controller.sendSvar(svar)
        val henvendelseArgumentCaptor = ArgumentCaptor.forClass(Henvendelse::class.java)
        Mockito.verify(service)?.sendSvar(henvendelseArgumentCaptor.capture(), ArgumentMatchers.anyString())
        MatcherAssert.assertThat(henvendelseArgumentCaptor.value.brukersEnhet, Is.`is`(brukersEnhet))
    }

    @Test
    fun senderIkkeFunnetNaarTraadOptionalIkkeErPresent() {
        Mockito.`when`(service!!.hentTraad(ArgumentMatchers.anyString())).thenReturn(null)
        val svar = Svar()
        svar.fritekst = "fritekst"
        svar.traadId = "0"
        val response = controller.sendSvar(svar)
        MatcherAssert.assertThat(response.status, Is.`is`(Response.Status.NOT_FOUND.statusCode))
    }

    @Test(expected = BadRequestException::class)
    fun smellerHvisTomFritekstISporsmal() {
        val sporsmal = Sporsmal()
        sporsmal.fritekst = ""
        sporsmal.temagruppe = Temagruppe.ARBD.name
        controller.sendSporsmal(sporsmal)
    }

    @Test(expected = BadRequestException::class)
    fun smellerHvisForLangFritekstISporsmal() {
        val sporsmal = Sporsmal()
        sporsmal.fritekst = StringUtils.join(Collections.nCopies(1001, 'a'), "")
        sporsmal.temagruppe = Temagruppe.ARBD.name
        controller.sendSporsmal(sporsmal)
    }

    @Test(expected = BadRequestException::class)
    fun smellerHvisAndreSosialtjenesterTemagruppeISporsmal() {
        val sporsmal = Sporsmal()
        sporsmal.fritekst = "DUMMY"
        sporsmal.temagruppe = Temagruppe.ANSOS.name
        controller.sendSporsmal(sporsmal)
    }

    @Test(expected = BadRequestException::class)
    fun smellerHvisBrukerErKode6OgTemagruppeOKSOS() {
        whenever(tilgangService.harTilgangTilKommunalInnsending(ArgumentMatchers.anyString())).thenReturn(
                TilgangDTO(TilgangDTO.Resultat.KODE6, "melding")
        )
        val sporsmal = Sporsmal()
        sporsmal.fritekst = "DUMMY"
        sporsmal.temagruppe = Temagruppe.OKSOS.name
        controller.sendSporsmal(sporsmal)
    }

    @Test(expected = BadRequestException::class)
    fun smellerHvisBrukerIkkeHarEnhetOgTemagruppeOKSOS() {
        whenever(tilgangService.harTilgangTilKommunalInnsending(ArgumentMatchers.anyString())).thenReturn(
                TilgangDTO(TilgangDTO.Resultat.INGEN_ENHET, "melding")
        )
        val sporsmal = Sporsmal()
        sporsmal.fritekst = "DUMMY"
        sporsmal.temagruppe = Temagruppe.OKSOS.name
        controller.sendSporsmal(sporsmal)
    }

    @Test(expected = BadRequestException::class)
    fun smellerHvisTemagruppeOKSOSOgUtledningFeiler() {
        whenever(tilgangService.harTilgangTilKommunalInnsending(ArgumentMatchers.anyString())).thenReturn(
                TilgangDTO(TilgangDTO.Resultat.FEILET, "melding")
        )
        val sporsmal = Sporsmal()
        sporsmal.fritekst = "DUMMY"
        sporsmal.temagruppe = Temagruppe.OKSOS.name
        controller.sendSporsmal(sporsmal)
    }
}
