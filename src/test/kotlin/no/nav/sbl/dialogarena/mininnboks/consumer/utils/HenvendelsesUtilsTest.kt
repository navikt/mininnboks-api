package no.nav.sbl.dialogarena.mininnboks.consumer.utils

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*
import no.nav.sbl.dialogarena.mininnboks.TestUtils
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstService
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstServiceImpl
import no.nav.sbl.dialogarena.mininnboks.consumer.TekstServiceImpl.hentTekst
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.joda.time.DateTime
import org.spekframework.spek2.Spek
import java.util.*

class HenvendelsesUtilsTest : Spek({

    test("transformerer Dokument Henvendelse") {
        every { tekstService.hentTekst(any()) } returns "value"
        val dokument = mockDokumentHenvendelse()

        val infoList = listOf(dokument)
        val henvendelserListe = infoList
            .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
        val dokumentHenvendelse = henvendelserListe[0]
        assertThat(dokumentHenvendelse.id, CoreMatchers.`is`(ID_1))
        assertThat(dokumentHenvendelse.traadId, CoreMatchers.`is`(ID_1))
        assertThat(dokumentHenvendelse.type, CoreMatchers.`is`(Henvendelsetype.DOKUMENT_VARSEL))
        assertThat(dokumentHenvendelse.lestDato, CoreMatchers.`is`(CoreMatchers.nullValue()))
        assertThat(dokumentHenvendelse.isLest, CoreMatchers.`is`(false))
        assertThat(dokumentHenvendelse.kanal, CoreMatchers.`is`(CoreMatchers.nullValue()))
        assertThat(dokumentHenvendelse.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
    }

    test("transformerer XMLHenvendelse Som Sporsmal Fra Bruker") {
        val info = mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, ID_1, ID_1)
        val infoList = listOf(info)
        val henvendelserListe = infoList
            .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
        val sporsmal = henvendelserListe[0]
        assertStandardFelter(sporsmal)
        assertThat(sporsmal.id, CoreMatchers.`is`(ID_1))
        assertThat(sporsmal.traadId, CoreMatchers.`is`(ID_1))
        assertThat(sporsmal.type, CoreMatchers.`is`(Henvendelsetype.SPORSMAL_SKRIFTLIG))
        assertThat(sporsmal.lestDato, CoreMatchers.`is`(Matchers.notNullValue()))
        assertThat(sporsmal.isLest, CoreMatchers.`is`(true))
        assertThat(sporsmal.kanal, CoreMatchers.`is`(CoreMatchers.nullValue()))
        assertThat(sporsmal.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
    }

    test("transformerer XMLHenvendelse Som Sporsmal Direkte Fra Bruker") {
        val info = mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE, ID_1, ID_1)
        val infoList = listOf(info)
        val henvendelserListe = infoList
            .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
        val sporsmal = henvendelserListe[0]
        assertThat(sporsmal.id, CoreMatchers.`is`(ID_1))
        assertThat(sporsmal.traadId, CoreMatchers.`is`(ID_1))
        assertThat(sporsmal.type, CoreMatchers.`is`(Henvendelsetype.SPORSMAL_SKRIFTLIG_DIREKTE))
        assertThat(sporsmal.lestDato, CoreMatchers.`is`(Matchers.notNullValue()))
        assertThat(sporsmal.isLest, CoreMatchers.`is`(true))
        assertThat(sporsmal.kanal, CoreMatchers.`is`(CoreMatchers.nullValue()))
        assertThat(sporsmal.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
    }

    test("transformerer XMLHenvendelse Ferdigstilt Uten Svar") {
        val info = mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, ID_1, ID_1)
        info.isFerdigstiltUtenSvar = true
        val infoList = listOf(info)
        val henvendelserListe = infoList
            .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
        val sporsmal = henvendelserListe[0]
        assertThat(sporsmal.ferdigstiltUtenSvar, CoreMatchers.`is`(true))
        assertThat(sporsmal.temagruppe?.name, CoreMatchers.`is`(Temagruppe.FMLI.name))
        assertThat(sporsmal.statusTekst, CoreMatchers.`is`("Beskjed – Familie"))
        assertThat(sporsmal.temagruppeNavn, CoreMatchers.`is`("Familie"))
    }

    test("transformerer XMLHenvendelse Som Svar Fra Bruker") {
        val info = mockXMLHenvendelseMedXMLMeldingFraBruker(XMLHenvendelseType.SVAR_SBL_INNGAAENDE, ID_2, ID_2)
        val infoList = listOf(info)
        val henvendelserListe = infoList
            .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
        val svar = henvendelserListe[0]
        assertStandardFelter(svar)
        assertThat(svar.id, CoreMatchers.`is`(ID_2))
        assertThat(svar.traadId, CoreMatchers.`is`(ID_2))
        assertThat(svar.type, CoreMatchers.`is`(Henvendelsetype.SVAR_SBL_INNGAAENDE))
        assertThat(svar.lestDato, CoreMatchers.`is`(Matchers.notNullValue()))
        assertThat(svar.isLest, CoreMatchers.`is`(true))
        assertThat(svar.kanal, CoreMatchers.`is`(CoreMatchers.nullValue()))
        assertThat(svar.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
        assertThat(svar.kontorsperreEnhet, CoreMatchers.`is`(KONTORSPERRE_ENHET))
    }

    test("transformerer XMLHenvendelse Som Sporsmal Til Bruker") {
        val info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE, ID_3, ID_3)
        val infoList = listOf(info)
        val henvendelserListe = infoList
            .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
        val sporsmal = henvendelserListe[0]
        assertStandardFelter(sporsmal)
        assertThat(sporsmal.id, CoreMatchers.`is`(ID_3))
        assertThat(sporsmal.traadId, CoreMatchers.`is`(ID_3))
        assertThat(sporsmal.type, CoreMatchers.`is`(Henvendelsetype.SPORSMAL_MODIA_UTGAAENDE))
        assertThat(sporsmal.lestDato, CoreMatchers.`is`(LEST_DATO))
        assertThat(sporsmal.isLest, CoreMatchers.`is`(true))
        assertThat(sporsmal.kanal, CoreMatchers.`is`(KANAL))
        assertThat(sporsmal.eksternAktor, CoreMatchers.`is`(NAVIDENT))
        assertThat(sporsmal.tilknyttetEnhet, CoreMatchers.`is`(TILKNYTTET_ENHET))
        assertThat(sporsmal.erTilknyttetAnsatt, CoreMatchers.`is`(ER_TILKNYTTET_ANSATT))
        assertThat(sporsmal.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
    }

    test("transformerer XMLHenvendelse Som Svar Til Bruker") {
        val info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.SVAR_SKRIFTLIG, ID_4, ID_1)
        val infoList = listOf(info)
        val henvendelserListe = infoList.map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
        val svar = henvendelserListe[0]
        assertThat(svar.id, CoreMatchers.`is`(ID_4))
        assertThat(svar.traadId, CoreMatchers.`is`(ID_1))
        assertThat(svar.type, CoreMatchers.`is`(Henvendelsetype.SVAR_SKRIFTLIG))
        assertThat(svar.lestDato, CoreMatchers.`is`(LEST_DATO))
        assertThat(svar.isLest, CoreMatchers.`is`(true))
        assertThat(svar.kanal, CoreMatchers.`is`(KANAL))
        assertThat(svar.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
    }

    test("transformerer XMLHenvendelse Som Referat Til Bruker") {
        val info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.REFERAT_OPPMOTE, ID_5, ID_5)
        val infoList = listOf(info)
        val henvendelserListe = infoList.map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
        val referat = henvendelserListe[0]
        assertStandardFelter(referat)
        assertThat(referat.id, CoreMatchers.`is`(ID_5))
        assertThat(referat.traadId, CoreMatchers.`is`(ID_5))
        assertThat(referat.type, CoreMatchers.`is`(Henvendelsetype.SAMTALEREFERAT_OPPMOTE))
        assertThat(referat.lestDato, CoreMatchers.`is`(LEST_DATO))
        assertThat(referat.isLest, CoreMatchers.`is`(true))
        assertThat(referat.kanal, CoreMatchers.`is`(KANAL))
        assertThat(referat.brukersEnhet, CoreMatchers.`is`(BRUKERS_ENHET))
    }

    test("hvis Innholdet Er Borte Blir Henvendelsen Merket Som Kassert") {
        val info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.REFERAT_OPPMOTE, ID_5, ID_5)
        info.metadataListe = null
        every { tekstService.hentTekst("innhold.kassert") } returns ("Innholdet er kassert")
        every { tekstService.hentTekst("temagruppe.kassert") } returns ("Kassert")
        val referat = HenvendelsesUtils.tilHenvendelse(info)
        assertThat(referat.fritekst, CoreMatchers.`is`("Innholdet i denne henvendelsen er kassert av NAV."))
        assertThat(referat.statusTekst, CoreMatchers.`is`("Kassert"))
        assertThat(referat.temagruppe, CoreMatchers.nullValue())
    }

    test("transformerer XMLHenvendelse Som Oppgave Varsel") {
        val info = mockXMLHenvendelseMedXMLOppgaveVarsel(XMLHenvendelseType.OPPGAVE_VARSEL, ID_5, ID_5)
        val henvendelse = HenvendelsesUtils.tilHenvendelse(info)
        assertThat(henvendelse.oppgaveType, CoreMatchers.`is`(OPPGAVE_TYPE))
        assertThat(henvendelse.oppgaveUrl, CoreMatchers.`is`(OPPGAVE_URL))
        assertThat(henvendelse.statusTekst, CoreMatchers.`is`("Oppgave"))
        assertThat(henvendelse.fritekst, CoreMatchers.`is`("Du har mottatt en oppgave."))
    }

    test("transformerer XmlHenvendelse Som Delvise Svar") {
        val info = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG, ID_2, ID_1)
        val henvendelse = HenvendelsesUtils.tilHenvendelse(info)
        assertThat(henvendelse.type, CoreMatchers.`is`(Henvendelsetype.DELVIS_SVAR_SKRIFTLIG))
    }

    test("returnerer DefaultKey Hvis Hent Tekst Kaster Exception") {

        val key = "nokkel"
        val defaultKey = "defaultKey"
        val defaulValue = "Value"
        TekstServiceImpl.tekster[key] = null
        TekstServiceImpl.tekster[defaultKey] = defaulValue

        val tekst = hentTekst(key, defaultKey)
        assertThat(tekst, CoreMatchers.`is`(defaulValue))
    }

    test("prover Ikke Aa Hente Status Tekst For DelviseSvar") {
        val henvendelse = mockXMLHenvendelseMedXMLMeldingTilBruker(XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG, ID_2, ID_1)
        HenvendelsesUtils.tilHenvendelse(henvendelse)
        verify(exactly = 0) { tekstService.hentTekst("status." + Henvendelsetype.DELVIS_SVAR_SKRIFTLIG.name) }
    }

    test("vasker Html I Fritekst Men Bevarer Line Endings") {
        val tekst = "<h1>Hei</h1> \n Hallo"
        val cleanTekst = HenvendelsesUtils.cleanOutHtml(tekst)
        assertThat(cleanTekst, CoreMatchers.`is`("Hei \n Hallo"))
    }
})

val ID_1 = "1"
val ID_2 = "2"
val ID_3 = "3"
val ID_4 = "4"
val ID_5 = "5"
val FRITEKST = "fritekst"
val OPPRETTET_DATO = TestUtils.date(GregorianCalendar(Calendar.YEAR, 1, 1))
val AVSLUTTET_DATO = TestUtils.date(GregorianCalendar(Calendar.YEAR, 1, 2))
val LEST_DATO = TestUtils.date(GregorianCalendar(Calendar.YEAR, 1, 3))
val OPPRETTET_DATO_JODA = DateTime(GregorianCalendar(Calendar.YEAR, 1, 1))
val AVSLUTTET_DATO_JODA = DateTime(GregorianCalendar(Calendar.YEAR, 1, 2))
val LEST_DATO_JODA = DateTime(GregorianCalendar(Calendar.YEAR, 1, 3))
val TEMAGRUPPE = Temagruppe.FMLI
val KANAL = "kanal"
val NAVIDENT = "navident"
val TILKNYTTET_ENHET = "tilknyttetEnhet"
val ER_TILKNYTTET_ANSATT = false
val REPETERENDE_VARSEL = false
val BRUKERS_ENHET = "1234"
val KONTORSPERRE_ENHET = "kontorsperreEnhet"
val OPPGAVE_URL = "oppgave/url"
val OPPGAVE_TYPE = "sykepenger"

fun assertStandardFelter(sporsmal: Henvendelse) {
    assertThat(sporsmal.fritekst, CoreMatchers.`is`(FRITEKST))
    assertThat(sporsmal.temagruppe, CoreMatchers.`is`(TEMAGRUPPE))
    assertThat(sporsmal.opprettet, CoreMatchers.`is`(OPPRETTET_DATO))
    assertThat(sporsmal.avsluttet, CoreMatchers.`is`(AVSLUTTET_DATO))
}

fun mockXMLHenvendelseMedXMLMeldingFraBruker(
    type: XMLHenvendelseType,
    id: String,
    kjedeId: String
): XMLHenvendelse {
    return XMLHenvendelse()
        .withHenvendelseType(type.name)
        .withBehandlingsId(id)
        .withBehandlingskjedeId(kjedeId)
        .withOpprettetDato(OPPRETTET_DATO_JODA)
        .withAvsluttetDato(AVSLUTTET_DATO_JODA)
        .withBrukersEnhet(BRUKERS_ENHET)
        .withKontorsperreEnhet(KONTORSPERRE_ENHET)
        .withMetadataListe(
            XMLMetadataListe().withMetadata(
                XMLMeldingFraBruker()
                    .withFritekst(FRITEKST)
                    .withTemagruppe(TEMAGRUPPE.name)
            )
        )
}

fun mockXMLHenvendelseMedXMLMeldingTilBruker(
    type: XMLHenvendelseType,
    id: String,
    kjedeId: String
): XMLHenvendelse {
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
        .withMetadataListe(
            XMLMetadataListe().withMetadata(
                XMLMeldingTilBruker()
                    .withFritekst(FRITEKST)
                    .withTemagruppe(TEMAGRUPPE.name)
                    .withKanal(KANAL)
                    .withNavident(NAVIDENT)
            )
        )
}

fun mockDokumentHenvendelse(): XMLHenvendelse {
    return XMLHenvendelse()
        .withHenvendelseType(XMLHenvendelseType.DOKUMENT_VARSEL.name)
        .withBehandlingsId(ID_1)
        .withBehandlingskjedeId(ID_1)
        .withOpprettetDato(OPPRETTET_DATO_JODA)
        .withAvsluttetDato(AVSLUTTET_DATO_JODA)
        .withBrukersEnhet(BRUKERS_ENHET)
        .withKontorsperreEnhet(KONTORSPERRE_ENHET)
        .withMetadataListe(
            XMLMetadataListe().withMetadata(
                XMLDokumentVarsel()
                    .withTemagruppe("OVRG")
                    .withFritekst("")
                    .withStoppRepeterendeVarsel(true)
            )
        )
}

fun mockXMLHenvendelseMedXMLOppgaveVarsel(type: XMLHenvendelseType, id: String, kjedeId: String): XMLHenvendelse {
    return XMLHenvendelse()
        .withHenvendelseType(type.name)
        .withBehandlingsId(id)
        .withBehandlingskjedeId(kjedeId)
        .withOpprettetDato(OPPRETTET_DATO_JODA)
        .withAvsluttetDato(AVSLUTTET_DATO_JODA)
        .withBrukersEnhet(BRUKERS_ENHET)
        .withKontorsperreEnhet(KONTORSPERRE_ENHET)
        .withMetadataListe(
            XMLMetadataListe().withMetadata(
                XMLOppgaveVarsel()
                    .withOppgaveType(OPPGAVE_TYPE)
                    .withFritekst("oppgave.$OPPGAVE_TYPE.fritekst")
                    .withTemagruppe(TEMAGRUPPE.name)
                    .withStoppRepeterendeVarsel(REPETERENDE_VARSEL)
                    .withOppgaveURL(OPPGAVE_URL)
            )
        )
}

val tekstService = mockk<TekstService>()
