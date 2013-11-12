package no.nav.sbl.dialogarena.minehenvendelser.sporsmal;

import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.tema.Tema;
import org.joda.time.DateTime;

import java.io.Serializable;

public class Sporsmal implements Serializable {

    private String fritekst;
    private Tema tema;
    public DateTime innsendingsTidspunkt;

    public void setTema(Tema tema) {
        this.tema = tema;
    }

    public void setFritekst(String fritekst) {
        this.fritekst = fritekst;
    }

    public Tema getTema() {
        return tema;
    }

    public String getFritekst() {
        return fritekst;
    }

    public boolean harTema() {
        return tema != null;
    }

}