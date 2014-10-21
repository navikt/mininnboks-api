package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebApplication;

import static no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelsetype.SPORSMAL_SKRIFTLIG;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.SAMTALEREFERAT;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.SVAR;

public class AvsenderBilde extends Image {

    public AvsenderBilde(String id, Henvendelse henvendelse) {
        super(id);
        String avsender = "", bilde = "";
        if (SVAR.contains(henvendelse.type) || SAMTALEREFERAT.contains(henvendelse.type)) {
            avsender = "nav";
            bilde = "nav-logo.svg";
        } else if (henvendelse.type == SPORSMAL_SKRIFTLIG) {
            avsender = "bruker";
            bilde = "siluett.svg";
        }
        add(new AttributeModifier("src", WebApplication.get().getServletContext().getContextPath() + "/img/" + bilde));
        add(new AttributeModifier("alt", new StringResourceModel("innboks.avsender." + avsender, this, null)));
    }
}
