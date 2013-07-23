package no.nav.sbl.dialogarena.minehenvendelser.components;

import java.io.Serializable;

public class SporsmalOgSvarVM implements Serializable {
    String tema, fritekst;

    public SporsmalOgSvarVM withTema(String tema) {
        this.tema = tema;
        return this;
    }
}
