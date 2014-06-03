package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelse;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.resource.ContextRelativeResource;

import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SPORSMAL;
import static no.nav.sbl.dialogarena.mininnboks.consumer.Henvendelsetype.SVAR;

public class AvsenderBilde extends Image {

    public AvsenderBilde(String id, Henvendelse henvendelse) {
        super(id);
        String avsender = "", bilde = "";
        if (henvendelse.type == SVAR || henvendelse.type == SAMTALEREFERAT) {
            avsender = "nav";
            bilde = "nav-logo.svg";
        } else if (henvendelse.type == SPORSMAL) {
            avsender = "bruker";
            bilde = "siluett.svg";
        }
        setImageResource(new ContextRelativeResource("img/" + bilde));
        add(new AttributeAppender("alt", new StringResourceModel("innboks.avsender." + avsender, this, null)));
    }
}
