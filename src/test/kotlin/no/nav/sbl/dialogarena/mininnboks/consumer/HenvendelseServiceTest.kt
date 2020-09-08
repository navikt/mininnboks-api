package no.nav.sbl.dialogarena.mininnboks.consumer

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*
import no.nav.sbl.dialogarena.mininnboks.TestUtils.lagHenvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
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

    private val henvendelsePortType: HenvendelsePortType? = mockk()
    private val sendInnHenvendelsePortType: SendInnHenvendelsePortType? = mockk()
    private val innsynHenvendelsePortType: InnsynHenvendelsePortType? = mockk()
    private val personService: PersonService? = mockk()
    private val tekstService: TekstService = mockk()
    private var henvendelseService: HenvendelseService? = mockk()

    val FNR = "fnr"
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
        henvendelseService = HenvendelseService.Default(henvendelsePortType!!, sendInnHenvendelsePortType!!, innsynHenvendelsePortType!!, personService!!)
        every { tekstService.hentTekst(any()) } returns "Tekst"
        val henvendelseListe: MutableList<Any> = ArrayList()
        henvendelseListe.add(lagHenvendelse(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE.name))
        every { henvendelsePortType.hentHenvendelseListe(any()) } returns
                WSHentHenvendelseListeResponse().withAny(henvendelseListe)
        every { sendInnHenvendelsePortType.sendInnHenvendelse(any()) } returns (WSSendInnHenvendelseResponse().withBehandlingsId("id"))
        every { personService.hentGeografiskTilknytning() } returns Optional.of(BRUKER_ENHET)
    }


    @Test
    fun `sender Inn Sporsmal Med Riktige Felter`() {
        val henvendelse = Henvendelse(FRITEKST, TEMAGRUPPE)
        henvendelseService!!.stillSporsmal(henvendelse, FNR)
        verify { sendInnHenvendelsePortType?.sendInnHenvendelse(capture(sendInnHenvendelseRequestArgumentCaptor)) }
        val request = sendInnHenvendelseRequestArgumentCaptor.captured
        MatcherAssert.assertThat(request.type, Matchers.`is`(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name))
        MatcherAssert.assertThat(request.fodselsnummer, Matchers.`is`(FNR))
        val xmlHenvendelse = request.any as XMLHenvendelse
        MatcherAssert.assertThat(xmlHenvendelse.henvendelseType, Matchers.`is`(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name))
        MatcherAssert.assertThat(xmlHenvendelse.opprettetDato, Matchers.`is`(Matchers.notNullValue()))
        MatcherAssert.assertThat(xmlHenvendelse.avsluttetDato, Matchers.`is`(Matchers.notNullValue()))
        MatcherAssert.assertThat(xmlHenvendelse.tema, Matchers.`is`(HenvendelseService.Default.KONTAKT_NAV_SAKSTEMA))
        MatcherAssert.assertThat(xmlHenvendelse.behandlingskjedeId, Matchers.`is`(Matchers.nullValue()))
        MatcherAssert.assertThat(xmlHenvendelse.brukersEnhet, Matchers.`is`(BRUKER_ENHET))
        val meldingFraBruker = xmlHenvendelse.metadataListe.metadata[0] as XMLMeldingFraBruker
        MatcherAssert.assertThat(meldingFraBruker.temagruppe, Matchers.`is`(TEMAGRUPPE.name))
        MatcherAssert.assertThat(meldingFraBruker.fritekst, Matchers.`is`(FRITEKST))
    }

    @Test
    fun `sender Inn Direkte Sporsmal Med Riktige Felter`() {
        val henvendelse = Henvendelse(FRITEKST, TEMAGRUPPE)
        henvendelseService!!.stillSporsmalDirekte(henvendelse, FNR)
        verify { sendInnHenvendelsePortType?.sendInnHenvendelse(capture(sendInnHenvendelseRequestArgumentCaptor)) }
        val request = sendInnHenvendelseRequestArgumentCaptor.captured
        MatcherAssert.assertThat(request.type, Matchers.`is`(XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE.name))
        MatcherAssert.assertThat(request.fodselsnummer, Matchers.`is`(FNR))
        val xmlHenvendelse = request.any as XMLHenvendelse
        MatcherAssert.assertThat(xmlHenvendelse.henvendelseType, Matchers.`is`(XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE.name))
        MatcherAssert.assertThat(xmlHenvendelse.opprettetDato, Matchers.`is`(Matchers.notNullValue()))
        MatcherAssert.assertThat(xmlHenvendelse.avsluttetDato, Matchers.`is`(Matchers.notNullValue()))
        MatcherAssert.assertThat(xmlHenvendelse.tema, Matchers.`is`(HenvendelseService.Default.KONTAKT_NAV_SAKSTEMA))
        MatcherAssert.assertThat(xmlHenvendelse.behandlingskjedeId, Matchers.`is`(Matchers.nullValue()))
        MatcherAssert.assertThat(xmlHenvendelse.brukersEnhet, Matchers.`is`(BRUKER_ENHET))
        val meldingFraBruker = xmlHenvendelse.metadataListe.metadata[0] as XMLMeldingFraBruker
        MatcherAssert.assertThat(meldingFraBruker.temagruppe, Matchers.`is`(TEMAGRUPPE.name))
        MatcherAssert.assertThat(meldingFraBruker.fritekst, Matchers.`is`(FRITEKST))
    }

    @Test
    fun `sender Inn Svar Med Riktige Felter`() {
        val henvendelse = Henvendelse(FRITEKST, TEMAGRUPPE)
        henvendelse.traadId = TRAAD_ID
        henvendelse.eksternAktor = EKSTERN_AKTOR
        henvendelse.brukersEnhet = BRUKER_ENHET
        henvendelse.tilknyttetEnhet = TILKNYTTET_ENHET
        henvendelse.erTilknyttetAnsatt = ER_TILKNYTTET_ANSATT
        henvendelse.kontorsperreEnhet = KONTORSPERRE_ENHET
        henvendelseService!!.sendSvar(henvendelse, FNR)
        verify {
            sendInnHenvendelsePortType?.sendInnHenvendelse(capture(sendInnHenvendelseRequestArgumentCaptor))
        }
        val request = sendInnHenvendelseRequestArgumentCaptor.captured
        MatcherAssert.assertThat(request.type, Matchers.`is`(XMLHenvendelseType.SVAR_SBL_INNGAAENDE.name))
        MatcherAssert.assertThat(request.fodselsnummer, Matchers.`is`(FNR))
        val xmlHenvendelse = request.any as XMLHenvendelse
        MatcherAssert.assertThat(xmlHenvendelse.henvendelseType, Matchers.`is`(XMLHenvendelseType.SVAR_SBL_INNGAAENDE.name))
        MatcherAssert.assertThat(xmlHenvendelse.opprettetDato, Matchers.`is`(Matchers.notNullValue()))
        MatcherAssert.assertThat(xmlHenvendelse.avsluttetDato, Matchers.`is`(Matchers.notNullValue()))
        MatcherAssert.assertThat(xmlHenvendelse.tema, Matchers.`is`(HenvendelseService.Default.KONTAKT_NAV_SAKSTEMA))
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
        henvendelseService!!.hentAlleHenvendelser(FNR)
        verify { henvendelsePortType?.hentHenvendelseListe(capture(hentHenvendelseListeRequestArgumentCaptor)) }
        val request = hentHenvendelseListeRequestArgumentCaptor.captured
        MatcherAssert.assertThat(request.fodselsnummer, Matchers.`is`(FNR))
    }

    @Test
    fun `spor Om Alle Henvendelsestyper Naar Den Henter Alle`() {
        henvendelseService!!.hentAlleHenvendelser(FNR)
        verify {
            henvendelsePortType?.hentHenvendelseListe(capture(hentHenvendelseListeRequestArgumentCaptor)) }
            val request = hentHenvendelseListeRequestArgumentCaptor.captured
            val values: List<XMLHenvendelseType> = ArrayList(Arrays.asList(*XMLHenvendelseType.values()))
                for (type in request.typer) {
                    MatcherAssert.assertThat(values.contains(XMLHenvendelseType.fromValue(type)), Matchers.`is`(true))
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
        every { henvendelsePortType!!.hentBehandlingskjede(any()) } returns WSHentBehandlingskjedeResponse().withAny(mockBehandlingskjedeMedDelsvar())
        henvendelseService!!.hentTraad(TRAAD_ID)
    }

    @Test
    fun `hent Alle Henvendelser Henter Delsvar`() {
        val argumentCaptor = slot<WSHentHenvendelseListeRequest>()
        every { henvendelsePortType!!.hentHenvendelseListe(capture(argumentCaptor)) } returns WSHentHenvendelseListeResponse()
        henvendelseService!!.hentAlleHenvendelser(FNR)
        MatcherAssert.assertThat(argumentCaptor.captured.typer, Matchers.hasItem(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG.name))
    }
}

