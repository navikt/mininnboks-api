package no.nav.sbl.dialogarena.mininnboks.sporsmal;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaModel;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.tema.Tema;
import org.joda.time.DateTime;

public class Sporsmal extends EnhancedTextAreaModel {

    private Tema tema;
    public DateTime innsendingsTidspunkt;

    public void setTema(Tema tema) {
        this.tema = tema;
    }

    public Tema getTema() {
        return tema;
    }

    public String getFritekst() {
        return text;
    }

    public boolean harTema() {
        return tema != null;
    }

}