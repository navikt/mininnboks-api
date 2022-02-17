package no.nav.sbl.dialogarena.mininnboks.consumer

import no.nav.common.auth.subject.Subject
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils
import no.nav.sbl.dialogarena.mininnboks.externalCall
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.innsynhenvendelse.InnsynHenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentBehandlingskjedeRequest
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest

interface HenvendelseService {
    suspend fun hentAlleHenvendelser(subject: Subject): List<Henvendelse>
    suspend fun hentTraad(behandlingskjedeId: String, subject: Subject): List<Henvendelse>
    suspend fun merkAlleSomLest(behandlingskjedeId: String, subject: Subject)
    suspend fun merkSomLest(id: String, subject: Subject)

    class Default(
        private val henvendelsePortType: HenvendelsePortType,
        private val innsynHenvendelsePortType: InnsynHenvendelsePortType
    ) : HenvendelseService {
        override suspend fun merkAlleSomLest(behandlingskjedeId: String, subject: Subject) {
            val traad = hentTraad(behandlingskjedeId, subject)
            val ids = traad
                .filter { henvendelse: Henvendelse -> !henvendelse.isLest }
                .map { henvendelse: Henvendelse -> henvendelse.id }
            externalCall(subject) {
                innsynHenvendelsePortType.merkSomLest(ids)
            }
        }

        override suspend fun merkSomLest(id: String, subject: Subject) {
            externalCall(subject) {
                innsynHenvendelsePortType.merkSomLest(listOf(id))
            }
        }

        override suspend fun hentAlleHenvendelser(subject: Subject): List<Henvendelse> {
            val wsHenvendelser = externalCall(subject) {
                henvendelsePortType.hentHenvendelseListe(
                    WSHentHenvendelseListeRequest()
                        .withFodselsnummer(subject.uid)
                        .withTyper(henvendelseTyper)
                )
                    .any
                    .map { obj: Any? -> XMLHenvendelse::class.java.cast(obj) }
            }
            return wsHenvendelser
                .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
        }

        override suspend fun hentTraad(behandlingskjedeId: String, subject: Subject): List<Henvendelse> {
            val wsBehandlingskjeder = externalCall(subject) {
                henvendelsePortType.hentBehandlingskjede(
                    WSHentBehandlingskjedeRequest().withBehandlingskjedeId(
                        behandlingskjedeId
                    )
                ).any
            }
            return wsBehandlingskjeder
                .map { obj: Any? -> XMLHenvendelse::class.java.cast(obj) }
                .map { wsMelding -> HenvendelsesUtils.tilHenvendelse(wsMelding) }
        }
    }

    companion object {
        const val KONTAKT_NAV_SAKSTEMA = "KNA"
        val henvendelseTyper = listOf(
            XMLHenvendelseType.DOKUMENT_VARSEL.name,
            XMLHenvendelseType.OPPGAVE_VARSEL.name
        )
    }
}
