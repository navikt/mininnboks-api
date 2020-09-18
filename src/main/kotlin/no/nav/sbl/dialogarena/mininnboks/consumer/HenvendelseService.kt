package no.nav.sbl.dialogarena.mininnboks.consumer

import no.nav.common.auth.subject.Subject
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils
import no.nav.sbl.dialogarena.mininnboks.externalCall
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.SendInnHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseRequest
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentBehandlingskjedeRequest
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest
import org.joda.time.DateTime
import java.util.stream.Collectors

interface HenvendelseService {
    suspend fun stillSporsmal(henvendelse: Henvendelse, subject: Subject): WSSendInnHenvendelseResponse
    suspend fun stillSporsmalDirekte(henvendelse: Henvendelse, subject: Subject): WSSendInnHenvendelseResponse
    suspend fun sendSvar(henvendelse: Henvendelse, subject: Subject): WSSendInnHenvendelseResponse?
    suspend fun hentAlleHenvendelser(subject: Subject): List<Henvendelse>
    suspend fun hentTraad(behandlingskjedeId: String?, subject: Subject): List<Henvendelse>
    suspend fun merkAlleSomLest(behandlingskjedeId: String?, subject: Subject)
    suspend fun merkSomLest(id: String, subject: Subject)

    class Default(private val henvendelsePortType: HenvendelsePortType,
                  private val sendInnHenvendelsePortType: SendInnHenvendelsePortType,
                  private val innsynHenvendelsePortType: InnsynHenvendelsePortType,
                  private val personService: PersonService) : HenvendelseService {


       override suspend fun stillSporsmal(henvendelse: Henvendelse, subject: Subject): WSSendInnHenvendelseResponse {
            return stillSporsmal(henvendelse, subject, XMLHenvendelseType.SPORSMAL_SKRIFTLIG)
        }

        override suspend fun stillSporsmalDirekte(henvendelse: Henvendelse, subject: Subject): WSSendInnHenvendelseResponse {
            return stillSporsmal(henvendelse, subject, XMLHenvendelseType.SPORSMAL_SKRIFTLIG_DIREKTE)
        }

        private suspend fun stillSporsmal(henvendelse: Henvendelse, subject: Subject, xmlHenvendelseType: XMLHenvendelseType): WSSendInnHenvendelseResponse {
            val enhet = personService.hentGeografiskTilknytning().orElse(null)
            val sporsmaltekst = HenvendelsesUtils.cleanOutHtml(HenvendelsesUtils.fjernHardeMellomrom(henvendelse.fritekst))

             return externalCall(subject){
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
             sendInnHenvendelsePortType.sendInnHenvendelse(
                     WSSendInnHenvendelseRequest()
                             .withType(xmlHenvendelseType.name)
                             .withFodselsnummer(subject.uid)
                             .withAny(info))
            }
        }

        override suspend fun sendSvar(henvendelse: Henvendelse, subject: Subject): WSSendInnHenvendelseResponse? {
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
            return externalCall(subject) {
                sendInnHenvendelsePortType.sendInnHenvendelse(WSSendInnHenvendelseRequest()
                        .withType(xmlHenvendelseType)
                        .withFodselsnummer(subject.uid)
                        .withAny(info))
            }
        }

        override suspend fun merkAlleSomLest(behandlingskjedeId: String?, subject: Subject) {
            val traad = hentTraad(behandlingskjedeId, subject)
            val ids = traad.stream()
                    .filter { henvendelse: Henvendelse -> !henvendelse.isLest }
                    .map { henvendelse: Henvendelse -> henvendelse.id }
                    .collect(Collectors.toList())
            innsynHenvendelsePortType.merkSomLest(ids)
        }

        override suspend fun merkSomLest(id: String, subject: Subject) {
            externalCall(subject) {
                innsynHenvendelsePortType.merkSomLest(listOf(id))
            }
        }

        override suspend fun hentAlleHenvendelser(subject: Subject): List<Henvendelse> {
            val typer = listOf(
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

            val wsHenvendelser = externalCall(subject) {
                henvendelsePortType.hentHenvendelseListe(
                        WSHentHenvendelseListeRequest()
                                .withFodselsnummer(subject.uid)
                                .withTyper(typer))
                        .any
                        .stream()
                        .map { obj: Any? -> obj as XMLHenvendelse }
            }

            return wsHenvendelser
                    .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
                    .collect(Collectors.toList())
        }

        override suspend fun hentTraad(behandlingskjedeId: String?, subject: Subject): List<Henvendelse> {
            val wsBehandlingskjeder = externalCall(subject) {
                henvendelsePortType.hentBehandlingskjede(WSHentBehandlingskjedeRequest().withBehandlingskjedeId(behandlingskjedeId)).any
            }
            return wsBehandlingskjeder
                    .map { obj: Any? -> XMLHenvendelse::class.java.cast(obj) }
                    .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
        }
    }

    companion object {
        const val KONTAKT_NAV_SAKSTEMA = "KNA"
    }
}
