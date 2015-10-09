package no.nav.sbl.dialogarena.mininnboks.consumer.domain;

import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;

import static java.util.Collections.reverseOrder;
import static no.nav.modig.lang.collections.ComparatorUtils.compareWith;

public class Henvendelse implements Serializable {

    public String id;
    public String traadId, fritekst, kanal, eksternAktor, tilknyttetEnhet, temagruppeNavn, statusTekst;
    public Henvendelsetype type;
    public Temagruppe temagruppe;
    public DateTime opprettet, avsluttet;
    public Boolean fraNav, fraBruker, kassert = false, erTilknyttetAnsatt;
    private DateTime lestDato;

    public Henvendelse(String id) {
        this.id = id;
    }

    public Henvendelse(String fritekst, Temagruppe temagruppe) {
        this.fritekst = fritekst;
        this.temagruppe = temagruppe;
    }

    public Henvendelse withTraadId(String traadId) {
        this.traadId = traadId;
        return this;
    }

    public Henvendelse withType(Henvendelsetype type) {
        this.type = type;
        return this;
    }

    public Henvendelse withOpprettetTid(DateTime opprettetTid) {
        this.opprettet = opprettetTid;
        return this;
    }

    public void markerSomLest(DateTime lestDato) {
        this.lestDato = lestDato;
    }

    public void markerSomLest() {
        this.lestDato = DateTime.now();
    }

    public DateTime getLestDato() {
        return lestDato;
    }

    public boolean isLest() {
        return lestDato != null;
    }

    public static final Transformer<Henvendelse, DateTime> OPPRETTET = new Transformer<Henvendelse, DateTime>() {
        @Override
        public DateTime transform(Henvendelse henvendelse) {
            return henvendelse.opprettet;
        }
    };

    public static final Transformer<Henvendelse, String> ID = new Transformer<Henvendelse, String>() {
        @Override
        public String transform(Henvendelse henvendelse) {
            return henvendelse.id;
        }
    };

    public static final Transformer<Henvendelse, String> TRAAD_ID = new Transformer<Henvendelse, String>() {
        @Override
        public String transform(Henvendelse henvendelse) {
            return henvendelse.traadId;
        }
    };

    public static final Transformer<Henvendelse, Boolean> ER_LEST = new Transformer<Henvendelse, Boolean>() {
        @Override
        public Boolean transform(Henvendelse henvendelse) {
            return henvendelse.isLest();
        }
    };

    public static final Comparator<Henvendelse> NYESTE_OVERST = reverseOrder(compareWith(OPPRETTET));

}
