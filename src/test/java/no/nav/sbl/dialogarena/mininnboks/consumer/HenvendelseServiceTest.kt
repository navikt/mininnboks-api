package no.nav.sbl.dialogarena.mininnboks.consumer

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils
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
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.runners.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class HenvendelseServiceTest {
    @Captor
    private val sendInnHenvendelseRequestArgumentCaptor: ArgumentCaptor<WSSendInnHenvendelseRequest>? = null

    @Captor
    private val hentHenvendelseListeRequestArgumentCaptor: ArgumentCaptor<WSHentHenvendelseListeRequest>? = null

    @Mock
    private val henvendelsePortType: HenvendelsePortType? = null

    @Mock
    private val sendInnHenvendelsePortType: SendInnHenvendelsePortType? = null

    @Mock
    private val innsynHenvendelsePortType: InnsynHenvendelsePortType? = null

    @Mock
    private val personService: PersonService? = null

    @Mock
    private val tekstService = Mockito.mock(TekstService::class.java)
    private var henvendelseService: HenvendelseService.Default? = null

    @Before
    fun setUp() {
        henvendelseService = HenvendelseService.Default(henvendelsePortType!!, sendInnHenvendelsePortType!!, innsynHenvendelsePortType!!, personService!!)
        setupTekstServiceMock()
        val henvendelseListe: MutableList<Any> = ArrayList()
        henvendelseListe.add(lagHenvendelse(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE.name).withBehandlingsId("id"))
        Mockito.`when`(henvendelsePortType.hentHenvendelseListe(ArgumentMatchers.any(WSHentHenvendelseListeRequest::class.java))).thenReturn(
                WSHentHenvendelseListeResponse().withAny(henvendelseListe))
        Mockito.`when`(sendInnHenvendelsePortType.sendInnHenvendelse(ArgumentMatchers.any(WSSendInnHenvendelseRequest::class.java)))
                .thenReturn(WSSendInnHenvendelseResponse().withBehandlingsId("id"))
        Mockito.`when`(personService.hentGeografiskTilknytning()).thenReturn(Optional.of(BRUKER_ENHET))
    }

    private fun setupTekstServiceMock() {
        Mockito.`when`(tekstService.hentTekst(ArgumentMatchers.anyString())).thenReturn("Tekst")
        HenvendelsesUtils.setTekstService(tekstService)
    }

    @After
    fun after() {
        HenvendelsesUtils.setTekstService(null)
    }

    @Test
    fun senderInnSporsmalMedRiktigeFelter() {
        val henvendelse = Henvendelse(FRITEKST, TEMAGRUPPE)
        henvendelseService!!.stillSporsmal(henvendelse, FNR)
        Mockito.verify(sendInnHenvendelsePortType)?.sendInnHenvendelse(sendInnHenvendelseRequestArgumentCaptor!!.capture())
        val request = sendInnHenvendelseRequestArgumentCaptor?.value
        MatcherAssert.assertThat(request?.type, Matchers.`is`(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name))
        MatcherAssert.assertThat(request?.fodselsnummer, Matchers.`is`(FNR))
        val xmlHenvendelse = request?.any as XMLHenvendelse
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
    fun senderInnDirekteSporsmalMedRiktigeFelter() {
        val henvendelse = Henvendelse(FRITEKST, TEMAGRUPPE)
        henvendelseService!!.stillSporsmalDirekte(henvendelse, FNR)
        Mockito.verify(sendInnHenvendelsePortType)?.sendInnHenvendelse(sendInnHenvendelseRequestArgumentCaptor!!.capture())
        val request = sendInnHenvendelseRequestArgumentCaptor?.value
        MatcherAssert.assertThat(request?.type, Matchers.`is`(XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE.name))
        MatcherAssert.assertThat(request?.fodselsnummer, Matchers.`is`(FNR))
        val xmlHenvendelse = request?.any as XMLHenvendelse
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
    fun senderInnSvarMedRiktigeFelter() {
        val henvendelse = Henvendelse(FRITEKST, TEMAGRUPPE)
        henvendelse.traadId = TRAAD_ID
        henvendelse.eksternAktor = EKSTERN_AKTOR
        henvendelse.brukersEnhet = BRUKER_ENHET
        henvendelse.tilknyttetEnhet = TILKNYTTET_ENHET
        henvendelse.erTilknyttetAnsatt = ER_TILKNYTTET_ANSATT
        henvendelse.kontorsperreEnhet = KONTORSPERRE_ENHET
        henvendelseService!!.sendSvar(henvendelse, FNR)
        Mockito.verify(sendInnHenvendelsePortType)?.sendInnHenvendelse(sendInnHenvendelseRequestArgumentCaptor!!.capture())
        val request = sendInnHenvendelseRequestArgumentCaptor?.value
        MatcherAssert.assertThat(request?.type, Matchers.`is`(XMLHenvendelseType.SVAR_SBL_INNGAAENDE.name))
        MatcherAssert.assertThat(request?.fodselsnummer, Matchers.`is`(FNR))
        val xmlHenvendelse = request?.any as XMLHenvendelse
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
    fun sporOmRiktigFodselsnummerNaarDenHenterAlle() {
        henvendelseService!!.hentAlleHenvendelser(FNR)
        Mockito.verify(henvendelsePortType)?.hentHenvendelseListe(hentHenvendelseListeRequestArgumentCaptor!!.capture())
        val request = hentHenvendelseListeRequestArgumentCaptor?.value
        MatcherAssert.assertThat(request?.fodselsnummer, Matchers.`is`(FNR))
    }

    @Test
    fun sporOmAlleHenvendelsestyperNaarDenHenterAlle() {
        henvendelseService!!.hentAlleHenvendelser(FNR)
        Mockito.verify(henvendelsePortType)?.hentHenvendelseListe(hentHenvendelseListeRequestArgumentCaptor!!.capture())
        val request = hentHenvendelseListeRequestArgumentCaptor?.value
        val values: List<XMLHenvendelseType> = ArrayList(Arrays.asList(*XMLHenvendelseType.values()))
        if (request != null) {
            for (type in request.typer) {
                MatcherAssert.assertThat(values.contains(XMLHenvendelseType.fromValue(type)), Matchers.`is`(true))
            }
        }
    }

    @Test
    fun hentTraadSomInneholderDelsvar() {
        Mockito.`when`(henvendelsePortType!!.hentBehandlingskjede(ArgumentMatchers.any()))
                .thenReturn(WSHentBehandlingskjedeResponse().withAny(mockBehandlingskjedeMedDelsvar()))
        henvendelseService!!.hentTraad(TRAAD_ID)
    }

    @Test
    fun hentAlleHenvendelserHenterDelsvar() {
        val argumentCaptor = ArgumentCaptor.forClass(WSHentHenvendelseListeRequest::class.java)
        Mockito.`when`(henvendelsePortType!!.hentHenvendelseListe(argumentCaptor.capture())).thenReturn(WSHentHenvendelseListeResponse())
        henvendelseService!!.hentAlleHenvendelser(FNR)
        MatcherAssert.assertThat(argumentCaptor.value.typer, Matchers.hasItem(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG.name))
    }

    private fun mockBehandlingskjedeMedDelsvar(): List<Any> {
        val henvendelseListe: MutableList<Any> = ArrayList()
        henvendelseListe.add(lagSporsmalSkriftlig())
        henvendelseListe.add(lagDelvisSvarSkriftlig())
        return henvendelseListe
    }

    private fun lagSporsmalSkriftlig(): XMLHenvendelse {
        return lagHenvendelse(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name)
                .withMetadataListe(XMLMetadataListe().withMetadata(XMLMeldingFraBruker()
                        .withFritekst("Jeg har et spørsmål")
                        .withTemagruppe(TEMAGRUPPE.name)))
    }

    private fun lagDelvisSvarSkriftlig(): XMLHenvendelse {
        return lagHenvendelse(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG.name)
                .withMetadataListe(XMLMetadataListe().withMetadata(XMLMeldingTilBruker()
                        .withFritekst("Delvis svar til deg")
                        .withTemagruppe(TEMAGRUPPE.name)))
    }

    private fun lagHenvendelse(type: String): XMLHenvendelse {
        return XMLHenvendelse().withHenvendelseType(type)
                .withBehandlingsId("id")
    }

    companion object {
        const val FNR = "fnr"
        val TEMAGRUPPE = Temagruppe.ARBD
        const val FRITEKST = "fritekst"
        const val TRAAD_ID = "traadId"
        const val EKSTERN_AKTOR = "eksternAktor"
        const val TILKNYTTET_ENHET = "tilknyttetEnhet"
        const val KONTORSPERRE_ENHET = "kontorsperreEnhet"
        const val BRUKER_ENHET = "brukersEnhet"
        const val ER_TILKNYTTET_ANSATT = false
    }
}
