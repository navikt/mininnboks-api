package no.nav.sbl.dialogarena.mininnboks.consumer

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.sendinnhenvendelse.meldinger.WSSendInnHenvendelseResponse

interface HenvendelseService {
    fun stillSporsmal(henvendelse: Henvendelse, fodselsnummer: String): WSSendInnHenvendelseResponse
    fun stillSporsmalDirekte(henvendelse: Henvendelse, fodselsnummer: String): WSSendInnHenvendelseResponse
    fun sendSvar(henvendelse: Henvendelse, uid: String?): WSSendInnHenvendelseResponse?
    fun hentAlleHenvendelser(fodselsnummer: String?): List<Henvendelse?>?
    fun hentTraad(behandlingskjedeId: String?): List<Henvendelse>
    fun merkAlleSomLest(behandlingskjedeId: String?)
    fun merkSomLest(id: String)

}
