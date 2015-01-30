package no.nav.sbl.dialogarena.mininnboks;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.*;
import org.joda.time.DateTime;

import static java.util.UUID.randomUUID;
import static org.joda.time.DateTime.now;

public class TestUtils {

    public static final String DEFAULT_EKSTERN_AKTOR = "eksternAktor";
    public static final String DEFAULT_TILKNYTTET_ENHET = "tilknyttetEnhet";
    public static final Henvendelsetype DEFAULT_TYPE = Henvendelsetype.SPORSMAL_SKRIFTLIG;
    public static final Temagruppe DEFAULT_TEMAGRUPPE = Temagruppe.ARBD;
    public static final DateTime DEFAULT_OPPRETTET = now();


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

    public static Henvendelse lagHenvendelse(String id, String traadId, DateTime opprettet) {
        return opprettHenvendelse(id, traadId, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, opprettet, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET);
    }

    public static Henvendelse lagHenvendelse(String traadId, DateTime opprettet) {
        return opprettHenvendelse(randomUUID().toString(), traadId, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, opprettet, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET);
    }

    public static Henvendelse lagForsteHenvendelseITraad() {
        String id = randomUUID().toString();
        return opprettHenvendelse(id, id, DEFAULT_TEMAGRUPPE, DEFAULT_TYPE, DEFAULT_OPPRETTET, DEFAULT_EKSTERN_AKTOR, DEFAULT_TILKNYTTET_ENHET);
    }

    public static Henvendelse lagForsteHenvendelseITraad(DateTime opprettet) {
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
                                                 Henvendelsetype type, DateTime opprettet, String eksternAktor, String tilknyttetEnhet) {
        Henvendelse henvendelse = new Henvendelse(id);
        henvendelse.temagruppe = temagruppe;
        henvendelse.traadId = traadId;
        henvendelse.type = type;
        henvendelse.opprettet = opprettet;
        henvendelse.eksternAktor = eksternAktor;
        henvendelse.tilknyttetEnhet = tilknyttetEnhet;
        return henvendelse;
    }
}
