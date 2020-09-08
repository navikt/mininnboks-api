package no.nav.sbl.dialogarena.mininnboks.consumer

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseRequest
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentBehandlingskjedeRequest
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest
import org.joda.time.DateTime
import java.util.*
import java.util.stream.Collectors

interface HenvendelseService {
    fun stillSporsmal(henvendelse: Henvendelse, fodselsnummer: String): WSSendInnHenvendelseResponse
    fun stillSporsmalDirekte(henvendelse: Henvendelse, fodselsnummer: String): WSSendInnHenvendelseResponse
    fun sendSvar(henvendelse: Henvendelse, uid: String?): WSSendInnHenvendelseResponse?
    fun hentAlleHenvendelser(fodselsnummer: String?): List<Henvendelse>
    fun hentTraad(behandlingskjedeId: String?): List<Henvendelse>
    fun merkAlleSomLest(behandlingskjedeId: String?)
    fun merkSomLest(id: String)

    class Default(private val henvendelsePortType: HenvendelsePortType,
                  private val sendInnHenvendelsePortType: SendInnHenvendelsePortType,
                  private val innsynHenvendelsePortType: InnsynHenvendelsePortType,
                  private val personService: PersonService) : HenvendelseService {


        override fun stillSporsmal(henvendelse: Henvendelse, fodselsnummer: String): WSSendInnHenvendelseResponse {
            return stillSporsmal(henvendelse, fodselsnummer, XMLHenvendelseType.SPORSMAL_SKRIFTLIG)
        }

        override fun stillSporsmalDirekte(henvendelse: Henvendelse, fodselsnummer: String): WSSendInnHenvendelseResponse {
            return stillSporsmal(henvendelse, fodselsnummer, XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE)
        }

        private fun stillSporsmal(henvendelse: Henvendelse, fodselsnummer: String, xmlHenvendelseType: XMLHenvendelseType): WSSendInnHenvendelseResponse {
            val enhet = personService.hentGeografiskTilknytning().orElse(null)
            val sporsmaltekst = HenvendelsesUtils.cleanOutHtml(HenvendelsesUtils.fjernHardeMellomrom(henvendelse.fritekst))
            val info = XMLHenvendelse()
                    .withHenvendelseType(xmlHenvendelseType.name)
                    .withOpprettetDato(DateTime.now())
                    .withAvsluttetDato(DateTime.now())
                    .withTema(KONTAKT_NAV_SAKSTEMA)
                    .withBehandlingskjedeId(null)
                    .withBrukersEnhet(enhet)
                    .withMetadataListe(XMLMetadataListe().withMetadata(
                            XMLMeldingFraBruker()
                                    .withTemagruppe(henvendelse.temagruppe?.name)
                                    .withFritekst(sporsmaltekst)))
            return sendInnHenvendelsePortType.sendInnHenvendelse(
                    WSSendInnHenvendelseRequest()
                            .withType(xmlHenvendelseType.name)
                            .withFodselsnummer(fodselsnummer)
                            .withAny(info))
        }

        override fun sendSvar(henvendelse: Henvendelse, fodselsnummer: String?): WSSendInnHenvendelseResponse? {
            val xmlHenvendelseType = XMLHenvendelseType.SVAR_SBL_INNGAAENDE.name
            val svartekst = HenvendelsesUtils.cleanOutHtml(HenvendelsesUtils.fjernHardeMellomrom(henvendelse.fritekst))
            val info = XMLHenvendelse()
                    .withHenvendelseType(xmlHenvendelseType)
                    .withOpprettetDato(DateTime.now())
                    .withAvsluttetDato(DateTime.now())
                    .withTema(KONTAKT_NAV_SAKSTEMA)
                    .withBehandlingskjedeId(henvendelse.traadId)
                    .withEksternAktor(henvendelse.eksternAktor)
                    .withTilknyttetEnhet(henvendelse.tilknyttetEnhet)
                    .withErTilknyttetAnsatt(henvendelse.erTilknyttetAnsatt)
                    .withBrukersEnhet(henvendelse.brukersEnhet)
                    .withKontorsperreEnhet(henvendelse.kontorsperreEnhet)
                    .withMetadataListe(XMLMetadataListe().withMetadata(
                            XMLMeldingFraBruker()
                                    .withTemagruppe(henvendelse.temagruppe!!.name)
                                    .withFritekst(svartekst)))
            return sendInnHenvendelsePortType.sendInnHenvendelse(WSSendInnHenvendelseRequest()
                            .withType(xmlHenvendelseType)
                            .withFodselsnummer(fodselsnummer)
                            .withAny(info))
        }

        override fun merkAlleSomLest(behandlingskjedeId: String?) {
            val traad = hentTraad(behandlingskjedeId)
            val ids = traad.stream()
                    .filter { henvendelse: Henvendelse -> !henvendelse.isLest }
                    .map { henvendelse: Henvendelse -> henvendelse.id }
                    .collect(Collectors.toList())
            innsynHenvendelsePortType.merkSomLest(ids)
        }

        override fun merkSomLest(id: String) {
            innsynHenvendelsePortType.merkSomLest(listOf(id))
        }

        override fun hentAlleHenvendelser(fodselsnummer: String?): List<Henvendelse> {
            val typer = Arrays.asList(
                    XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name,
                    XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE.name,
                    XMLHenvendelseType.SVAR_SKRIFTLIG.name,
                    XMLHenvendelseType.SVAR_OPPMOTE.name,
                    XMLHenvendelseType.SVAR_TELEFON.name,
                    XMLHenvendelseType.REFERAT_OPPMOTE.name,
                    XMLHenvendelseType.REFERAT_TELEFON.name,
                    XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE.name,
                    XMLHenvendelseType.INFOMELDING_MODIA_UTGAAENDE.name,
                    XMLHenvendelseType.SVAR_SBL_INNGAAENDE.name,
                    XMLHenvendelseType.DOKUMENT_VARSEL.name,
                    XMLHenvendelseType.OPPGAVE_VARSEL.name,
                    XMLHenvendelseType.DELVIS_SVAR_SKRIFTLIG.name)
            val wsHenvendelser = henvendelsePortType.hentHenvendelseListe(
                            WSHentHenvendelseListeRequest()
                                    .withFodselsnummer(fodselsnummer)
                                    .withTyper(typer))
                    .any
                    .stream()
                    .map { obj: Any? -> XMLHenvendelse::class.java.cast(obj) }
            return wsHenvendelser
                    .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
                    .collect(Collectors.toList())
        }

        override fun hentTraad(behandlingskjedeId: String?): List<Henvendelse> {
            val wsBehandlingskjeder = henvendelsePortType.hentBehandlingskjede(WSHentBehandlingskjedeRequest().withBehandlingskjedeId(behandlingskjedeId)).any
            return wsBehandlingskjeder
                    .map { obj: Any? -> XMLHenvendelse::class.java.cast(obj) }
                    .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
        }

        companion object {
            const val KONTAKT_NAV_SAKSTEMA = "KNA"
        }
    }
}
