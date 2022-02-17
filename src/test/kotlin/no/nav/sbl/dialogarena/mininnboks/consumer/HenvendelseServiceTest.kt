package no.nav.sbl.dialogarena.mininnboks.consumer

import io.mockk.*
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentBehandlingskjedeResponse
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.spekframework.spek2.Spek

private val henvendelseListe: MutableList<Any> = java.util.ArrayList()
private val hentHenvendelseListeRequestArgumentCaptor = slot<WSHentHenvendelseListeRequest>()
private val henvendelsePortType: HenvendelsePortType = mockk()
private val innsynHenvendelsePortType: InnsynHenvendelsePortType = mockk()
private val tekstService: TekstService = mockk()
private var henvendelseService: HenvendelseService = mockk()
private var unleashService: UnleashService = mockk()

private val FNR = "fnr"
private val subject = Subject(FNR, IdentType.EksternBruker, SsoToken.oidcToken("fnr", emptyMap<String, Any>()))
private val TEMAGRUPPE = Temagruppe.ARBD
private val TRAAD_ID = "traadId"

object HenvendelseServiceTest : Spek({

    beforeEachTest {
        henvendelseService = HenvendelseService.Default(
            henvendelsePortType,
            innsynHenvendelsePortType
        )
        coEvery { tekstService.hentTekst(any()) } returns "Tekst"
        henvendelseListe.add(lagHenvendelse(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE.name))
        coEvery { henvendelsePortType.hentHenvendelseListe(any()) } returns
            WSHentHenvendelseListeResponse().withAny(henvendelseListe)
        every { unleashService.isEnabled(any()) } returns false
    }

    afterEachTest {
        // Det er nødvendig  å reset count til 0 i mockk system etter hvert test ellers  coVerify(exactly) skal svare feil.
        clearAllMocks()
    }

    test("spor Om Riktig Fodselsnummer Naar Den Henter Alle") {
        runBlocking {
            henvendelseService.hentAlleHenvendelser(subject)
            verify { henvendelsePortType.hentHenvendelseListe(capture(hentHenvendelseListeRequestArgumentCaptor)) }
            val request = hentHenvendelseListeRequestArgumentCaptor.captured
            assertThat(request.fodselsnummer, Matchers.`is`(FNR))
        }
    }

    test("spor Om Alle Henvendelsestyper Naar Den Henter Alle") {
        runBlocking {
            henvendelseService.hentAlleHenvendelser(subject)
            verify {
                henvendelsePortType.hentHenvendelseListe(capture(hentHenvendelseListeRequestArgumentCaptor))
            }
            val request = hentHenvendelseListeRequestArgumentCaptor.captured
            val values: List<XMLHenvendelseType> = ArrayList(listOf(*XMLHenvendelseType.values()))
            for (type in request.typer) {
                assertThat(values.contains(XMLHenvendelseType.fromValue(type)), Matchers.`is`(true))
            }
        }
    }

    test("hent Traad Som Inne holder Delsvar") {
        runBlocking {
            coEvery { henvendelsePortType.hentBehandlingskjede(any()) } returns WSHentBehandlingskjedeResponse().withAny(
                mockBehandlingskjedeMedDelsvar()
            )
            henvendelseService.hentTraad(TRAAD_ID, subject)
        }
    }

    test("hent Alle Henvendelser Henter Delsvar") {
        runBlocking {
            val argumentCaptor = slot<WSHentHenvendelseListeRequest>()
            coEvery { henvendelsePortType.hentHenvendelseListe(capture(argumentCaptor)) } returns WSHentHenvendelseListeResponse()
            henvendelseService.hentAlleHenvendelser(subject)
            assertThat(argumentCaptor.captured.typer, Matchers.hasItem(XMLHenvendelseType.DOKUMENT_VARSEL.name))
            assertThat(argumentCaptor.captured.typer, Matchers.hasItem(XMLHenvendelseType.OPPGAVE_VARSEL.name))
        }
    }
})

private fun lagHenvendelse(type: String): XMLHenvendelse {
    return XMLHenvendelse().withHenvendelseType(type)
        .withBehandlingsId("id")
}

private fun lagSporsmalSkriftlig(): XMLHenvendelse {
    return lagHenvendelse(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name)
        .withMetadataListe(
            XMLMetadataListe().withMetadata(
                XMLMeldingFraBruker()
                    .withFritekst("Jeg har et spørsmål")
                    .withTemagruppe(TEMAGRUPPE.name)
            )
        )
}

private fun lagDelvisSvarSkriftlig(): XMLHenvendelse {
    return lagHenvendelse(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG.name)
        .withMetadataListe(
            XMLMetadataListe().withMetadata(
                XMLMeldingTilBruker()
                    .withFritekst("Delvis svar til deg")
                    .withTemagruppe(TEMAGRUPPE.name)
            )
        )
}

private fun mockBehandlingskjedeMedDelsvar(): List<Any> {
    val henvendelseListe: MutableList<Any> = ArrayList()
    henvendelseListe.add(lagSporsmalSkriftlig())
    henvendelseListe.add(lagDelvisSvarSkriftlig())
    return henvendelseListe
}
