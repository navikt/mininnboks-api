package no.nav.sbl.dialogarena.mininnboks.sporsmal;

import no.nav.modig.wicket.component.enhancedtextarea.EnhancedTextAreaModel;
import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Temagruppe;
import org.joda.time.DateTime;

public class Sporsmal extends EnhancedTextAreaModel {

    private Temagruppe temagruppe;
    public DateTime innsendingsTidspunkt;
    public boolean betingelserAkseptert = false;

    public void setTemagruppe(Temagruppe temagruppe) {
        this.temagruppe = temagruppe;
    }

    public Temagruppe getTemagruppe() {
        return temagruppe;
    }

    public String getFritekst() {
        return text;
    }

}