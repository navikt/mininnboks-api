package no.nav.sbl.dialogarena.mininnboks.innboks;

import no.nav.sbl.dialogarena.mininnboks.consumer.domain.Henvendelse;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebApplication;

import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.INNGAAENDE;
import static no.nav.sbl.dialogarena.mininnboks.consumer.utils.HenvendelsesUtils.UTGAAENDE;

public class AvsenderBilde extends Image {

    public AvsenderBilde(String id, Henvendelse henvendelse) {
        super(id);
        String avsender = "", bilde = "";
        if (UTGAAENDE.contains(henvendelse.type)) {
            avsender = "nav";
            bilde = "nav-logo.svg";
        } else if (INNGAAENDE.contains(henvendelse.type)) {
            avsender = "bruker";
            bilde = "siluett.svg";
        }
        add(new AttributeModifier("src", WebApplication.get().getServletContext().getContextPath() + "/img/" + bilde));
        add(new AttributeModifier("alt", new StringResourceModel("innboks.avsender." + avsender, this, null)));
    }
}
