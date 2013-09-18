package no.nav.sbl.dialogarena.minehenvendelser.henvendelser.sendsporsmal;

import java.io.Serializable;

import org.joda.time.DateTime;

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

}