package no.nav.sbl.dialogarena.mininnboks

import no.nav.common.auth.subject.IdentType
import no.nav.common.auth.subject.SsoToken
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import no.nav.common.auth.subject.Subject

object TestUtils {
    val MOCK_SUBJECT = Subject("uid", IdentType.EksternBruker, SsoToken.oidcToken("token", HashMap<String, Any?>()))
    const val DEFAULT_EKSTERN_AKTOR = "eksternAktor"
    const val DEFAULT_TILKNYTTET_ENHET = "tilknyttetEnhet"
    val DEFAULT_TYPE = Henvendelsetype.SPORSMAL_SKRIFTLIG
    val DEFAULT_TEMAGRUPPE = Temagruppe.ARBD
    val DEFAULT_OPPRETTET = now()
    fun lagHenvendelse(erLest: Boolean): Henvendelse {
        val henvendelse = lagForsteHenvendelseITraad()
        if (erLest) {
            henvendelse.markerSomLest()
        }
        return henvendelse
    }

    fun lagHenvendelse(id: String?, traadId: String?): Henvendelse {
        return opprettHenvendelse(id, traadId, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, DEFAULT_OPPRETTET, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET)
    }

    fun lagHenvendelse(id: String?, traadId: String?, opprettet: Date?): Henvendelse {
        return opprettHenvendelse(id, traadId, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, opprettet, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET)
    }

    @JvmOverloads
    fun lagHenvendelse(traadId: String?, opprettet: Date? = now()): Henvendelse {
        return opprettHenvendelse(UUID.randomUUID().toString(), traadId, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, opprettet, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET)
    }

    fun lagForsteHenvendelseITraad(): Henvendelse {
        val id = UUID.randomUUID().toString()
        return opprettHenvendelse(id, id, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, DEFAULT_OPPRETTET, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET)
    }

    fun lagForsteHenvendelseITraad(opprettet: Date?): Henvendelse {
        val id = UUID.randomUUID().toString()
        return opprettHenvendelse(id, id, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, opprettet, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET)
    }

    fun lagForsteHenvendelseITraad(type: Henvendelsetype?, lest: Boolean): Henvendelse {
        val id = UUID.randomUUID().toString()
        val henvendelse = opprettHenvendelse(id, id, DEFAULT_TEMAGRUPPE, type, DEFAULT_OPPRETTET, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET)
        if (lest) {
            henvendelse.markerSomLest()
        }
        return henvendelse
    }

    fun opprettHenvendelse(id: String?, traadId: String?, temagruppe: Temagruppe?,
                           type: Henvendelsetype?, opprettet: Date?, eksternAktor: String?, tilknyttetEnhet: String?): Henvendelse {
        val henvendelse = Henvendelse(id)
        henvendelse.temagruppe = temagruppe
        henvendelse.traadId = traadId
        henvendelse.type = type
        henvendelse.opprettet = opprettet
        henvendelse.eksternAktor = eksternAktor
        henvendelse.tilknyttetEnhet = tilknyttetEnhet
        return henvendelse
    }

    fun nowPlus(days: Int): Date {
        return Date(Instant.now().plus(days.toLong(), ChronoUnit.DAYS).toEpochMilli())
    }

    fun now(): Date {
        return Date()
    }

    fun date(gregorianCalendar: GregorianCalendar): Date {
        return Date(gregorianCalendar.timeInMillis)
    }
}
