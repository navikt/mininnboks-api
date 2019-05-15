package no.nav.sbl.dialogarena.mininnboks;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.util.UUID.randomUUID;

public class TestUtils {

    public static final String DEFAULT_EKSTERN_AKTOR = "eksternAktor";
    public static final String DEFAULT_TILKNYTTET_ENHET = "tilknyttetEnhet";
    public static final Henvendelsetype DEFAULT_TYPE = Henvendelsetype.SPORSMAL_SKRIFTLIG;
    public static final Temagruppe DEFAULT_TEMAGRUPPE = Temagruppe.ARBD;
    public static final Date DEFAULT_OPPRETTET = now();


    public static Henvendelse lagHenvendelse(boolean erLest) {
        Henvendelse henvendelse = lagForsteHenvendelseITraad();
        if (erLest) {
            henvendelse.markerSomLest();
        }
        return henvendelse;
    }

    public static Henvendelse lagHenvendelse(String traadId) {
        return lagHenvendelse(traadId, now());
    }

    public static Henvendelse lagHenvendelse(String id, String traadId) {
        return opprettHenvendelse(id, traadId, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, DEFAULT_OPPRETTET, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET);
    }

    public static Henvendelse lagHenvendelse(String id, String traadId, Date opprettet) {
        return opprettHenvendelse(id, traadId, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, opprettet, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET);
    }

    public static Henvendelse lagHenvendelse(String traadId, Date opprettet) {
        return opprettHenvendelse(randomUUID().toString(), traadId, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, opprettet, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET);
    }

    public static Henvendelse lagForsteHenvendelseITraad() {
        String id = randomUUID().toString();
        return opprettHenvendelse(id, id, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, DEFAULT_OPPRETTET, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET);
    }

    public static Henvendelse lagForsteHenvendelseITraad(Date opprettet) {
        String id = randomUUID().toString();
        return opprettHenvendelse(id, id, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, opprettet, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET);
    }

    public static Henvendelse lagForsteHenvendelseITraad(Henvendelsetype type, boolean lest) {
        String id = randomUUID().toString();
        Henvendelse henvendelse = opprettHenvendelse(id, id, DEFAULT_TEMAGRUPPE, type, DEFAULT_OPPRETTET, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET);
        if (lest) {
            henvendelse.markerSomLest();
        }
        return henvendelse;
    }

    public static Henvendelse opprettHenvendelse(String id, String traadId, Temagruppe temagruppe,
                                                 Henvendelsetype type, Date opprettet, String eksternAktor, String tilknyttetEnhet) {
        Henvendelse henvendelse = new Henvendelse(id);
        henvendelse.temagruppe = temagruppe;
        henvendelse.traadId = traadId;
        henvendelse.type = type;
        henvendelse.opprettet = opprettet;
        henvendelse.eksternAktor = eksternAktor;
        henvendelse.tilknyttetEnhet = tilknyttetEnhet;
        return henvendelse;
    }

    public static Date nowPlus(int days){
        return new Date(Instant.now().plus(days, ChronoUnit.DAYS).toEpochMilli());
    }

    public static Date now(){
        return new Date();
    }

    public static Date date(GregorianCalendar gregorianCalendar) {
        return new Date(gregorianCalendar.getTimeInMillis());
    }

}
