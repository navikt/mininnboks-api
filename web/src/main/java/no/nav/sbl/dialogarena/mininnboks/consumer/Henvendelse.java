package no.nav.sbl.dialogarena.mininnboks.consumer;

import java.io.Serializable;
import java.util.Comparator;

import no.nav.sbl.dialogarena.mininnboks.sporsmal.temagruppe.Temagruppe;
import org.joda.time.DateTime;

public class Henvendelse implements Serializable {

    public Henvendelse(String id) {
        this.id = id;
    }
    public final String id;
    public String traadId, fodselsnummer, fritekst, kanal;
    public Henvendelsetype type;
    public Temagruppe temagruppe;
    public DateTime opprettet, avsluttet;
    private DateTime lestDato;

    public void markerSomLest(DateTime lestDato) {
        this.lestDato = lestDato;
    }

    public void markerSomLest(){
        this.lestDato = DateTime.now();
    }

    public DateTime getLestDato() {
        return lestDato;
    }

    public boolean erLest() {
        return lestDato != null;
    }

    public static final Comparator<Henvendelse> NYESTE_OVERST = new Comparator<Henvendelse>() {
        public int compare(Henvendelse h1, Henvendelse h2) {
            return h2.opprettet.compareTo(h1.opprettet);
        }
    };
}
