package no.nav.sbl.dialogarena.minehenvendelser.components.sporsmalogsvar;

import java.io.Serializable;

public class Sporsmal implements Serializable {
    private String tema, fritekst;

    public void setTema(String tema) {
        this.tema = tema;
    }

    public void setFritekst(String fritekst) {
        this.fritekst = fritekst;
    }

    public String getTema() {
        return tema;
    }

    public String getFritekst() {
        return fritekst;
    }

}