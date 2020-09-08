package no.nav.sbl.dialogarena.mininnboks.consumer.utils

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*
import no.nav.sbl.dialogarena.mininnboks.TestUtils
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstService
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.joda.time.DateTime
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import java.util.*
import java.util.stream.Collectors

class HenvendelsesUtilsTest {
    val tekstService = mockk<TekstService>()


    @Test
    fun `transformerer Dokument Henvendelse`() {
        every {tekstService.hentTekst(ArgumentMatchers.anyString())}  returns "value"

        val dokument = mockDokumentHenvendelse();

        val infoList = listOf(dokument)
        val henvendelserListe = infoList.stream()
                .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
                .collect(Collectors.toList())
        val dokumentHenvendelse = henvendelserListe[0]
        Assert.assertThat(dokumentHenvendelse?.id, CoreMatchers.`is`(ID_1))
        Assert.assertThat(dokumentHenvendelse?.traadId, CoreMatchers.`is`(ID_1))
        Assert.assertThat(dokumentHenvendelse?.type, CoreMatchers.`is`(Henvendelsetype.DOKUMENT_VARSEL))
        Assert.assertThat(dokumentHenvendelse?.lestDato, CoreMatchers.`is`(CoreMatchers.nullValue()))
        Assert.assertThat(dokumentHenvendelse?.isLest, CoreMatchers.`is`(false))
        Assert.assertThat(dokumentHenvendelse?.kanal, CoreMatchers.`is`(CoreMatchers.nullValue()))
        Assert.assertThat(dokumentHenvendelse?.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
    }

    @Test
    fun `transformerer XMLHenvendelse Som Sporsmal Fra Bruker`() {
        val info = mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, ID_1, ID_1)
        val infoList = listOf(info)
        val henvendelserListe = infoList.stream()
                .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
                .collect(Collectors.toList())
        val sporsmal = henvendelserListe[0]
        sporsmal?.let { assertStandardFelter(it) }
        Assert.assertThat(sporsmal?.id, CoreMatchers.`is`(ID_1))
        Assert.assertThat(sporsmal?.traadId, CoreMatchers.`is`(ID_1))
        Assert.assertThat(sporsmal?.type, CoreMatchers.`is`(Henvendelsetype.SPORSMAL_SKRIFTLIG))
        Assert.assertThat(sporsmal?.lestDato, CoreMatchers.`is`(Matchers.notNullValue()))
        Assert.assertThat(sporsmal?.isLest, CoreMatchers.`is`(true))
        Assert.assertThat(sporsmal?.kanal, CoreMatchers.`is`(CoreMatchers.nullValue()))
        Assert.assertThat(sporsmal?.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
    }

    @Test
    fun `transformerer XMLHenvendelse Som Sporsmal Direkte Fra Bruker`() {
        val info = mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE, ID_1, ID_1)
        val infoList = listOf(info)
        val henvendelserListe = infoList.stream()
                .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
                .collect(Collectors.toList())
        val sporsmal = henvendelserListe[0]
        sporsmal?.let { assertStandardFelter(it) }
        Assert.assertThat(sporsmal?.id, CoreMatchers.`is`(ID_1))
        Assert.assertThat(sporsmal?.traadId, CoreMatchers.`is`(ID_1))
        Assert.assertThat(sporsmal?.type, CoreMatchers.`is`(Henvendelsetype.SPORSMAL_SKRIFTLIG_DIREKTE))
        Assert.assertThat(sporsmal?.lestDato, CoreMatchers.`is`(Matchers.notNullValue()))
        Assert.assertThat(sporsmal?.isLest, CoreMatchers.`is`(true))
        Assert.assertThat(sporsmal?.kanal, CoreMatchers.`is`(CoreMatchers.nullValue()))
        Assert.assertThat(sporsmal?.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
    }

    @Test
    fun `transformerer XMLHenvendelse Ferdigstilt Uten Svar`() {
        val info = mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, ID_1, ID_1)
        info.isFerdigstiltUtenSvar = true
        val infoList = listOf(info)
        val henvendelserListe = infoList.stream()
                .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
                .collect(Collectors.toList())
        val sporsmal = henvendelserListe[0]
        Assert.assertThat(sporsmal?.ferdigstiltUtenSvar, CoreMatchers.`is`(true))
    }

    @Test
    fun `transformerer XMLHenvendelse Som Svar Fra Bruker`() {
        val info = mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType.SVAR_SBL_INNGAAENDE, ID_2, ID_2)
        val infoList = listOf(info)
        val henvendelserListe = infoList.stream()
                .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
                .collect(Collectors.toList())
        val svar = henvendelserListe[0]
        svar?.let { assertStandardFelter(it) }
        Assert.assertThat(svar?.id, CoreMatchers.`is`(ID_2))
        Assert.assertThat(svar?.traadId, CoreMatchers.`is`(ID_2))
        Assert.assertThat(svar?.type, CoreMatchers.`is`(Henvendelsetype.SVAR_SBL_INNGAAENDE))
        Assert.assertThat(svar?.lestDato, CoreMatchers.`is`(Matchers.notNullValue()))
        Assert.assertThat(svar?.isLest, CoreMatchers.`is`(true))
        Assert.assertThat(svar?.kanal, CoreMatchers.`is`(CoreMatchers.nullValue()))
        Assert.assertThat(svar?.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
        Assert.assertThat(svar?.kontorsperreEnhet, CoreMatchers.`is`(KONTORSPERRE_ENHET))
    }

    @Test
    fun `transformerer XMLHenvendelse Som Sporsmal Til Bruker`() {
        val info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE, ID_3, ID_3)
        val infoList = listOf(info)
        val henvendelserListe = infoList.stream()
                .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
                .collect(Collectors.toList())
        val sporsmal = henvendelserListe[0]
        if (sporsmal != null) {
            assertStandardFelter(sporsmal)
        }
        Assert.assertThat(sporsmal?.id, CoreMatchers.`is`(ID_3))
        Assert.assertThat(sporsmal?.traadId, CoreMatchers.`is`(ID_3))
        Assert.assertThat(sporsmal?.type, CoreMatchers.`is`(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE))
        Assert.assertThat(sporsmal?.lestDato, CoreMatchers.`is`(LEST_DATO))
        Assert.assertThat(sporsmal?.isLest, CoreMatchers.`is`(true))
        Assert.assertThat(sporsmal?.kanal, CoreMatchers.`is`(KANAL))
        Assert.assertThat(sporsmal?.eksternAktor, CoreMatchers.`is`(NAVIDENT))
        Assert.assertThat(sporsmal?.tilknyttetEnhet, CoreMatchers.`is`(TILKNYTTET_ENHET))
        Assert.assertThat(sporsmal?.erTilknyttetAnsatt, CoreMatchers.`is`(ER_TILKNYTTET_ANSATT))
        Assert.assertThat(sporsmal?.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
    }

    @Test
    fun `transformerer XMLHenvendelse Som Svar Til Bruker`() {
        val info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.SVAR_SKRIFTLIG, ID_4, ID_1)
        val infoList = listOf(info)
        val henvendelserListe = infoList.stream().map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }.collect(Collectors.toList())
        val svar = henvendelserListe[0]
        svar?.let { assertStandardFelter(it) }
        Assert.assertThat(svar?.id, CoreMatchers.`is`(ID_4))
        Assert.assertThat(svar?.traadId, CoreMatchers.`is`(ID_1))
        Assert.assertThat(svar?.type, CoreMatchers.`is`(Henvendelsetype.SVAR_SKRIFTLIG))
        Assert.assertThat(svar?.lestDato, CoreMatchers.`is`(LEST_DATO))
        Assert.assertThat(svar?.isLest, CoreMatchers.`is`(true))
        Assert.assertThat(svar?.kanal, CoreMatchers.`is`(KANAL))
        Assert.assertThat(svar?.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
    }

    @Test
    fun `transformerer XMLHenvendelse Som Referat Til Bruker`() {
        val info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.REFERAT_OPPMOTE, ID_5, ID_5)
        val infoList = listOf(info)
        val henvendelserListe = infoList.stream().map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }.collect(Collectors.toList())
        val referat = henvendelserListe[0]
        referat?.let { assertStandardFelter(it) }
        Assert.assertThat(referat?.id, CoreMatchers.`is`(ID_5))
        Assert.assertThat(referat?.traadId, CoreMatchers.`is`(ID_5))
        Assert.assertThat(referat?.type, CoreMatchers.`is`(Henvendelsetype.SAMTALEREFERAT_OPPMOTE))
        Assert.assertThat(referat?.lestDato, CoreMatchers.`is`(LEST_DATO))
        Assert.assertThat(referat?.isLest, CoreMatchers.`is`(true))
        Assert.assertThat(referat?.kanal, CoreMatchers.`is`(KANAL))
        Assert.assertThat(referat?.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
    }

    private fun assertStandardFelter(sporsmal: Henvendelse) {
        Assert.assertThat(sporsmal.fritekst, CoreMatchers.`is`(FRITEKST))
        Assert.assertThat(sporsmal.temagruppe, CoreMatchers.`is`(TEMAGRUPPE))
        Assert.assertThat(sporsmal.opprettet, CoreMatchers.`is`(OPPRETTET_DATO))
        Assert.assertThat(sporsmal.avsluttet, CoreMatchers.`is`(AVSLUTTET_DATO))
    }

    @Test
    fun `hvis Innholdet Er Borte Blir Henvendelsen Merket Som Kassert`() {
        val info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.REFERAT_OPPMOTE, ID_5, ID_5)
        info.metadataListe = null
        every {tekstService.hentTekst("innhold.kassert") } returns ("Innholdet er kassert")
        every {tekstService.hentTekst("temagruppe.kassert") }returns ("Kassert")
        val referat = HenvendelsesUtils.tilHenvendelse(info)
        Assert.assertThat(referat?.fritekst, CoreMatchers.`is`("Innholdet  i  denne  henvendelsen  er  kassert  av  NAV."))
        Assert.assertThat(referat?.statusTekst, CoreMatchers.`is`("Kassert"))
        Assert.assertThat(referat?.temagruppe, CoreMatchers.nullValue())
    }

    @Test
    fun `transformerer XMLHenvendelse Som Oppgave Varsel`() {
        val info = mockXMLHenvendelseMedXMLOppgaveVarsel(XMLHenvendelseType.OPPGAVE_VARSEL, ID_5, ID_5)
        val henvendelse = HenvendelsesUtils.tilHenvendelse(info)
        Assert.assertThat(henvendelse.oppgaveType, CoreMatchers.`is`(OPPGAVE_TYPE))
        Assert.assertThat(henvendelse.oppgaveUrl, CoreMatchers.`is`(OPPGAVE_URL))
        Assert.assertThat(henvendelse.statusTekst, CoreMatchers.`is`("Oppgave"))
        Assert.assertThat(henvendelse.fritekst, CoreMatchers.`is`("Du  har  mottatt  en  oppgave."))
    }

    @Test
    fun `transformerer XmlHenvendelse Som Delvise Svar`() {
        val info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG, ID_2, ID_1)
        val henvendelse = HenvendelsesUtils.tilHenvendelse(info)
        Assert.assertThat(henvendelse.type, CoreMatchers.`is`(Henvendelsetype.DELVIS_SVAR_SKRIFTLIG))
    }

    @Test
    fun `returnerer DefaultKey Hvis Hent Tekst Kaster Exception` () {
        val key = "nokkel"
        val defaultKey = "defaultKey"
        every {(tekstService.hentTekst(key)) } throws NullPointerException()
        every {(tekstService.hentTekst(defaultKey)) } returns(defaultKey)
        val tekst = HenvendelsesUtils.hentTekst(tekstService, key, defaultKey)
        Assert.assertThat(tekst, CoreMatchers.`is`(defaultKey))
    }

    @Test
    fun `prover Ikke Aa Hente Status Tekst For DelviseSvar`() {
        val henvendelse = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG, ID_2, ID_1)
        HenvendelsesUtils.tilHenvendelse(henvendelse)
        verify(exactly = 0)  { tekstService.hentTekst("status." + Henvendelsetype.DELVIS_SVAR_SKRIFTLIG.name)}
    }

    @Test
    fun `vasker Html I Fritekst Men Bevarer Line Endings`() {
        val tekst = "<h1>Hei</h1> \n Hallo"
        val cleanTekst = HenvendelsesUtils.cleanOutHtml(tekst)
        Assert.assertThat(cleanTekst, CoreMatchers.`is`("Hei \n Hallo"))
    }

    private fun mockXMLHenvendelseMedXMLMeldingFraBruker(type: XMLHenvendelseType, id: String, kjedeId: String): XMLHenvendelse {
        return XMLHenvendelse()
                .withHenvendelseType(type.name)
                .withBehandlingsId(id)
                .withBehandlingskjedeId(kjedeId)
                .withOpprettetDato(OPPRETTET_DATO_JODA)
                .withAvsluttetDato(AVSLUTTET_DATO_JODA)
                .withBrukersEnhet(BRUKERS_ENHET)
                .withKontorsperreEnhet(KONTORSPERRE_ENHET)
                .withMetadataListe(XMLMetadataListe().withMetadata(
                        XMLMeldingFraBruker()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMAGRUPPE.name)
                ))
    }

    private fun mockXMLHenvendelseMedXMLMeldingTilBruker(type: XMLHenvendelseType, id: String, kjedeId: String): XMLHenvendelse {
        return XMLHenvendelse()
                .withHenvendelseType(type.name)
                .withBehandlingsId(id)
                .withBehandlingskjedeId(kjedeId)
                .withOpprettetDato(OPPRETTET_DATO_JODA)
                .withAvsluttetDato(AVSLUTTET_DATO_JODA)
                .withLestDato(LEST_DATO_JODA)
                .withEksternAktor(NAVIDENT)
                .withBrukersEnhet(BRUKERS_ENHET)
                .withTilknyttetEnhet(TILKNYTTET_ENHET)
                .withErTilknyttetAnsatt(ER_TILKNYTTET_ANSATT)
                .withMetadataListe(XMLMetadataListe().withMetadata(
                        XMLMeldingTilBruker()
                                .withFritekst(FRITEKST)
                                .withTemagruppe(TEMAGRUPPE.name)
                                .withKanal(KANAL)
                                .withNavident(NAVIDENT)
                ))
    }

    private fun mockDokumentHenvendelse(): XMLHenvendelse {
        return XMLHenvendelse()
                .withHenvendelseType(XMLHenvendelseType.DOKUMENT_VARSEL.name)
                .withBehandlingsId(ID_1)
                .withBehandlingskjedeId(ID_1)
                .withOpprettetDato(OPPRETTET_DATO_JODA)
                .withAvsluttetDato(AVSLUTTET_DATO_JODA)
                .withBrukersEnhet(BRUKERS_ENHET)
                .withKontorsperreEnhet(KONTORSPERRE_ENHET)
                .withMetadataListe(XMLMetadataListe().withMetadata(
                        XMLDokumentVarsel()
                                .withTemagruppe("OVRG")
                                .withFritekst("")
                                .withStoppRepeterendeVarsel(true)
                ))
    }

    private fun mockXMLHenvendelseMedXMLOppgaveVarsel(type: XMLHenvendelseType, id: String, kjedeId: String): XMLHenvendelse {
        return XMLHenvendelse()
                .withHenvendelseType(type.name)
                .withBehandlingsId(id)
                .withBehandlingskjedeId(kjedeId)
                .withOpprettetDato(OPPRETTET_DATO_JODA)
                .withAvsluttetDato(AVSLUTTET_DATO_JODA)
                .withBrukersEnhet(BRUKERS_ENHET)
                .withKontorsperreEnhet(KONTORSPERRE_ENHET)
                .withMetadataListe(XMLMetadataListe().withMetadata(
                        XMLOppgaveVarsel()
                                .withOppgaveType(OPPGAVE_TYPE)
                                .withFritekst("oppgave.$OPPGAVE_TYPE.fritekst")
                                .withTemagruppe(TEMAGRUPPE.name)
                                .withStoppRepeterendeVarsel(REPETERENDE_VARSEL)
                                .withOppgaveURL(OPPGAVE_URL)
                ))
    }

    companion object {
        private const val ID_1 = "1"
        private const val ID_2 = "2"
        private const val ID_3 = "3"
        private const val ID_4 = "4"
        private const val ID_5 = "5"
        private const val FRITEKST = "fritekst"
        private val OPPRETTET_DATO = TestUtils.date(GregorianCalendar(Calendar.YEAR, 1, 1))
        private val AVSLUTTET_DATO = TestUtils.date(GregorianCalendar(Calendar.YEAR, 1, 2))
        private val LEST_DATO = TestUtils.date(GregorianCalendar(Calendar.YEAR, 1, 3))
        private val OPPRETTET_DATO_JODA = DateTime(GregorianCalendar(Calendar.YEAR, 1, 1))
        private val AVSLUTTET_DATO_JODA = DateTime(GregorianCalendar(Calendar.YEAR, 1, 2))
        private val LEST_DATO_JODA = DateTime(GregorianCalendar(Calendar.YEAR, 1, 3))
        private val TEMAGRUPPE = Temagruppe.FMLI
        private const val KANAL = "kanal"
        private const val NAVIDENT = "navident"
        private const val TILKNYTTET_ENHET = "tilknyttetEnhet"
        private const val ER_TILKNYTTET_ANSATT = false
        private const val REPETERENDE_VARSEL = false
        private const val BRUKERS_ENHET = "1234"
        private const val KONTORSPERRE_ENHET = "kontorsperreEnhet"
        private const val OPPGAVE_URL = "oppgave/url"
        private const val OPPGAVE_TYPE = "sykepenger"
    }
}
