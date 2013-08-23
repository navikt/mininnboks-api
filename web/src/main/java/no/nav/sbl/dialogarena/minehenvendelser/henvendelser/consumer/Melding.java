package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.consumer;

import java.io.Serializable;
import org.joda.time.DateTime;

public class Melding implements Serializable {

    public Melding(String id, Meldingstype type, String traadId) {
        this.id = id;
        this.type = type;
        this.traadId = traadId;
    }
    public final String id, traadId;
    public final Meldingstype type;
    public String tema, overskrift, fritekst;
    public DateTime opprettet, lestDato;
    private boolean lest;

    public void markerSomLest() {
        lest = true;
        lestDato = DateTime.now();
    }

    public boolean erLest() {
        return lest;
    }
}
