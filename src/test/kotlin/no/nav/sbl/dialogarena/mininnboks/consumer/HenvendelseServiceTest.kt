package no.nav.sbl.dialogarena.mininnboks.consumer

import io.mockk.*
import kotlinx.coroutines.runBlocking
import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.common.auth.subject.Subject
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseRequest
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentBehandlingskjedeResponse
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*


class HenvendelseServiceTest {

    private val sendInnHenvendelseRequestArgumentCaptor = slot<WSSendInnHenvendelseRequest>()

    private val hentHenvendelseListeRequestArgumentCaptor = slot<WSHentHenvendelseListeRequest>()

    private val henvendelsePortType: HenvendelsePortType = mockk()
    private val sendInnHenvendelsePortType: SendInnHenvendelsePortType = mockk()
    private val innsynHenvendelsePortType: InnsynHenvendelsePortType = mockk()
    private val personService: PersonService = mockk()
    private val tekstService: TekstService = mockk()
    private var henvendelseService: HenvendelseService = mockk()
    val FNR = "fnr"

    private val subject = Subject(FNR, IdentType.EksternBruker, SsoToken.oidcToken("fnr", emptyMap<String, Any>()))

    val TEMAGRUPPE = Temagruppe.ARBD
    val FRITEKST = "fritekst"
    val TRAAD_ID = "traadId"
    val EKSTERN_AKTOR = "eksternAktor"
    val TILKNYTTET_ENHET = "tilknyttetEnhet"
    val KONTORSPERRE_ENHET = "kontorsperreEnhet"
    val BRUKER_ENHET = "brukersEnhet"
    val ER_TILKNYTTET_ANSATT = false

    @BeforeEach
    fun setUp() {
        henvendelseService = HenvendelseService.Default(henvendelsePortType, sendInnHenvendelsePortType, innsynHenvendelsePortType, personService)
        coEvery { tekstService.hentTekst(any()) } returns "Tekst"
        val henvendelseListe: MutableList<Any> = ArrayList()
        henvendelseListe.add(lagHenvendelse(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE.name))
        coEvery { henvendelsePortType.hentHenvendelseListe(any()) } returns
                WSHentHenvendelseListeResponse().withAny(henvendelseListe)
        coEvery { sendInnHenvendelsePortType.sendInnHenvendelse(any()) } returns (WSSendInnHenvendelseResponse().withBehandlingsId("id"))
        coEvery { personService.hentGeografiskTilknytning(any()) } returns BRUKER_ENHET
    }


    @Test
    fun `sender Inn Sporsmal Med Riktige Felter`() {
        runBlocking {
            val henvendelse = Henvendelse(fritekst = FRITEKST, temagruppe = TEMAGRUPPE, type = Henvendelsetype.SPORSMAL_SKRIFTLIG)

            henvendelseService.stillSporsmal(henvendelse, null, subject)

            verify { sendInnHenvendelsePortType.sendInnHenvendelse(capture(sendInnHenvendelseRequestArgumentCaptor)) }
            coVerify { personService.hentGeografiskTilknytning(any()) }
            val request = sendInnHenvendelseRequestArgumentCaptor.captured
            val xmlHenvendelse = request.any as XMLHenvendelse
            val meldingFraBruker = xmlHenvendelse.metadataListe.metadata[0] as XMLMeldingFraBruker

            MatcherAssert.assertThat(request.type, Matchers.`is`(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name))
            MatcherAssert.assertThat(request.fodselsnummer, Matchers.`is`(FNR))
            MatcherAssert.assertThat(xmlHenvendelse.henvendelseType, Matchers.`is`(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name))
            MatcherAssert.assertThat(xmlHenvendelse.opprettetDato, Matchers.`is`(Matchers.notNullValue()))
            MatcherAssert.assertThat(xmlHenvendelse.avsluttetDato, Matchers.`is`(Matchers.notNullValue()))
            MatcherAssert.assertThat(xmlHenvendelse.tema, Matchers.`is`(HenvendelseService.KONTAKT_NAV_SAKSTEMA))
            MatcherAssert.assertThat(xmlHenvendelse.behandlingskjedeId, Matchers.`is`(Matchers.nullValue()))
            MatcherAssert.assertThat(xmlHenvendelse.brukersEnhet, Matchers.`is`(BRUKER_ENHET))
            MatcherAssert.assertThat(meldingFraBruker.temagruppe, Matchers.`is`(TEMAGRUPPE.name))
            MatcherAssert.assertThat(meldingFraBruker.fritekst, Matchers.`is`(FRITEKST))
        }
    }

    @Test
    fun `henter ikke ut GT om overstyring er satt`() {
        runBlocking {
            val overstyrtGt = "010101"
            val henvendelse = Henvendelse(fritekst = FRITEKST, temagruppe = TEMAGRUPPE, type = Henvendelsetype.SPORSMAL_SKRIFTLIG)

            henvendelseService.stillSporsmal(henvendelse, overstyrtGt, subject)

            verify { sendInnHenvendelsePortType.sendInnHenvendelse(capture(sendInnHenvendelseRequestArgumentCaptor)) }
            coVerify(exactly = 0) { personService.hentGeografiskTilknytning(any()) }
            val request = sendInnHenvendelseRequestArgumentCaptor.captured
            val xmlHenvendelse = request.any as XMLHenvendelse
            MatcherAssert.assertThat(xmlHenvendelse.brukersEnhet, Matchers.`is`(overstyrtGt))
        }
    }

    @Test
    fun `sender Inn Direkte Sporsmal Med Riktige Felter`() {
        val henvendelse = Henvendelse(fritekst = FRITEKST, temagruppe = TEMAGRUPPE, type = Henvendelsetype.SPORSMAL_SKRIFTLIG)

        runBlocking {
            henvendelseService.stillSporsmalDirekte(henvendelse, subject)
            verify { sendInnHenvendelsePortType.sendInnHenvendelse(capture(sendInnHenvendelseRequestArgumentCaptor)) }
            val request = sendInnHenvendelseRequestArgumentCaptor.captured
            MatcherAssert.assertThat(request.type, Matchers.`is`(XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE.name))
            MatcherAssert.assertThat(request.fodselsnummer, Matchers.`is`(FNR))
            val xmlHenvendelse = request.any as XMLHenvendelse
            MatcherAssert.assertThat(xmlHenvendelse.henvendelseType, Matchers.`is`(XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE.name))
            MatcherAssert.assertThat(xmlHenvendelse.opprettetDato, Matchers.`is`(Matchers.notNullValue()))
            MatcherAssert.assertThat(xmlHenvendelse.avsluttetDato, Matchers.`is`(Matchers.notNullValue()))
            MatcherAssert.assertThat(xmlHenvendelse.tema, Matchers.`is`(HenvendelseService.KONTAKT_NAV_SAKSTEMA))
            MatcherAssert.assertThat(xmlHenvendelse.behandlingskjedeId, Matchers.`is`(Matchers.nullValue()))
            MatcherAssert.assertThat(xmlHenvendelse.brukersEnhet, Matchers.`is`(BRUKER_ENHET))
            val meldingFraBruker = xmlHenvendelse.metadataListe.metadata[0] as XMLMeldingFraBruker
            MatcherAssert.assertThat(meldingFraBruker.temagruppe, Matchers.`is`(TEMAGRUPPE.name))
            MatcherAssert.assertThat(meldingFraBruker.fritekst, Matchers.`is`(FRITEKST))
        }
    }

    @Test
    fun `sender Inn Svar Med Riktige Felter`() {
        val henvendelse = Henvendelse(
                fritekst = FRITEKST,
                temagruppe = TEMAGRUPPE,
                traadId = TRAAD_ID,
                eksternAktor = EKSTERN_AKTOR,
                brukersEnhet = BRUKER_ENHET,
                tilknyttetEnhet = TILKNYTTET_ENHET,
                erTilknyttetAnsatt = ER_TILKNYTTET_ANSATT,
                kontorsperreEnhet = KONTORSPERRE_ENHET,
                type = Henvendelsetype.SVAR_SKRIFTLIG)
        runBlocking {
            henvendelseService.sendSvar(henvendelse, subject)
            verify {
                sendInnHenvendelsePortType.sendInnHenvendelse(capture(sendInnHenvendelseRequestArgumentCaptor))
            }
        }
        val request = sendInnHenvendelseRequestArgumentCaptor.captured
        MatcherAssert.assertThat(request.type, Matchers.`is`(XMLHenvendelseType.SVAR_SBL_INNGAAENDE.name))
        MatcherAssert.assertThat(request.fodselsnummer, Matchers.`is`(FNR))
        val xmlHenvendelse = request.any as XMLHenvendelse
        MatcherAssert.assertThat(xmlHenvendelse.henvendelseType, Matchers.`is`(XMLHenvendelseType.SVAR_SBL_INNGAAENDE.name))
        MatcherAssert.assertThat(xmlHenvendelse.opprettetDato, Matchers.`is`(Matchers.notNullValue()))
        MatcherAssert.assertThat(xmlHenvendelse.avsluttetDato, Matchers.`is`(Matchers.notNullValue()))
        MatcherAssert.assertThat(xmlHenvendelse.tema, Matchers.`is`(HenvendelseService.KONTAKT_NAV_SAKSTEMA))
        MatcherAssert.assertThat(xmlHenvendelse.behandlingskjedeId, Matchers.`is`(TRAAD_ID))
        MatcherAssert.assertThat(xmlHenvendelse.eksternAktor, Matchers.`is`(EKSTERN_AKTOR))
        MatcherAssert.assertThat(xmlHenvendelse.tilknyttetEnhet, Matchers.`is`(TILKNYTTET_ENHET))
        MatcherAssert.assertThat(xmlHenvendelse.isErTilknyttetAnsatt, Matchers.`is`(ER_TILKNYTTET_ANSATT))
        MatcherAssert.assertThat(xmlHenvendelse.brukersEnhet, Matchers.`is`(BRUKER_ENHET))
        MatcherAssert.assertThat(xmlHenvendelse.kontorsperreEnhet, Matchers.`is`(KONTORSPERRE_ENHET))
        val meldingFraBruker = xmlHenvendelse.metadataListe.metadata[0] as XMLMeldingFraBruker
        MatcherAssert.assertThat(meldingFraBruker.temagruppe, Matchers.`is`(TEMAGRUPPE.name))
        MatcherAssert.assertThat(meldingFraBruker.fritekst, Matchers.`is`(FRITEKST))
    }

    @Test
    fun `spor Om Riktig Fodselsnummer Naar Den Henter Alle`() {
        runBlocking {
            henvendelseService.hentAlleHenvendelser(subject)
            verify { henvendelsePortType.hentHenvendelseListe(capture(hentHenvendelseListeRequestArgumentCaptor)) }
            val request = hentHenvendelseListeRequestArgumentCaptor.captured
            MatcherAssert.assertThat(request.fodselsnummer, Matchers.`is`(FNR))
        }
    }

    @Test
    fun `spor Om Alle Henvendelsestyper Naar Den Henter Alle`() {
        runBlocking {
            henvendelseService.hentAlleHenvendelser(subject)
            verify {
                henvendelsePortType.hentHenvendelseListe(capture(hentHenvendelseListeRequestArgumentCaptor))
            }
            val request = hentHenvendelseListeRequestArgumentCaptor.captured
            val values: List<XMLHenvendelseType> = ArrayList(listOf(*XMLHenvendelseType.values()))
            for (type in request.typer) {
                MatcherAssert.assertThat(values.contains(XMLHenvendelseType.fromValue(type)), Matchers.`is`(true))
            }
        }
    }


    fun lagHenvendelse(type: String): XMLHenvendelse {
        return XMLHenvendelse().withHenvendelseType(type)
                .withBehandlingsId("id")
    }

    fun lagSporsmalSkriftlig(): XMLHenvendelse {
        return lagHenvendelse(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name)
                .withMetadataListe(XMLMetadataListe().withMetadata(XMLMeldingFraBruker()
                        .withFritekst("Jeg har et spørsmål")
                        .withTemagruppe(TEMAGRUPPE.name)))
    }

    fun lagDelvisSvarSkriftlig(): XMLHenvendelse {
        return lagHenvendelse(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG.name)
                .withMetadataListe(XMLMetadataListe().withMetadata(XMLMeldingTilBruker()
                        .withFritekst("Delvis svar til deg")
                        .withTemagruppe(TEMAGRUPPE.name)))
    }


    fun mockBehandlingskjedeMedDelsvar(): List<Any> {
        val henvendelseListe: MutableList<Any> = ArrayList()
        henvendelseListe.add(lagSporsmalSkriftlig())
        henvendelseListe.add(lagDelvisSvarSkriftlig())
        return henvendelseListe
    }


    @Test
    fun `hent Traad Som Inne holder Delsvar`() {
        runBlocking {
            coEvery { henvendelsePortType.hentBehandlingskjede(any()) } returns WSHentBehandlingskjedeResponse().withAny(mockBehandlingskjedeMedDelsvar())
            henvendelseService.hentTraad(TRAAD_ID, subject)
        }
    }

    @Test
    fun `hent Alle Henvendelser Henter Delsvar`() {
        runBlocking {
            val argumentCaptor = slot<WSHentHenvendelseListeRequest>()
            coEvery { henvendelsePortType.hentHenvendelseListe(capture(argumentCaptor)) } returns WSHentHenvendelseListeResponse()
            henvendelseService.hentAlleHenvendelser(subject)
            MatcherAssert.assertThat(argumentCaptor.captured.typer, Matchers.hasItem(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG.name))
        }
    }
}

