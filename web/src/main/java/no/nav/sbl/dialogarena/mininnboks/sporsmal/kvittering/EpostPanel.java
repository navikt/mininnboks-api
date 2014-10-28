package no.nav.sbl.dialogarena.mininnboks.sporsmal.kvittering;

import no.nav.sbl.dialogarena.mininnboks.consumer.EpostService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class EpostPanel extends Panel {

    private static final Logger LOG = LoggerFactory.getLogger(EpostPanel.class);

    @Inject
    private EpostService epostService;

    public EpostPanel(String id) {
        super(id);

        WebMarkupContainer harEpostContainer = new WebMarkupContainer("harEpostContainer");
        WebMarkupContainer manglerEpostContainer = new WebMarkupContainer("manglerEpostContainer");
        WebMarkupContainer tpsUtilgjengeligContainer = new WebMarkupContainer("tpsUtilgjengeligContainer");


        String brukersEpostadresse = null;
        try {
            brukersEpostadresse = epostService.hentEpostadresse();

            harEpostContainer.setVisibilityAllowed(!brukersEpostadresse.isEmpty());
            manglerEpostContainer.setVisibilityAllowed(brukersEpostadresse.isEmpty());
            tpsUtilgjengeligContainer.setVisibilityAllowed(false);
        } catch (Exception e) {
            LOG.warn("Kall mot TPS feilet", e);
            harEpostContainer.setVisibilityAllowed(false);
            manglerEpostContainer.setVisibilityAllowed(false);
            tpsUtilgjengeligContainer.setVisibilityAllowed(true);
        }
        BrukerprofilLink tilEndringAvEpost = new BrukerprofilLink("tilEndringAvEpost");
        tilEndringAvEpost.add(AttributeModifier.replace("aria-label", "Endre mailen: "+brukersEpostadresse));

        harEpostContainer.add(new Label("epostAdresse", brukersEpostadresse));
        harEpostContainer.add(tilEndringAvEpost);
        add(harEpostContainer);

        manglerEpostContainer.add(new BrukerprofilLink("tilRegistreringAvEpost"));
        add(manglerEpostContainer);

        tpsUtilgjengeligContainer.add(new BrukerprofilLink("tilBrukerprofil"));
        add(tpsUtilgjengeligContainer);
    }

    private static class BrukerprofilLink extends ExternalLink {

        public BrukerprofilLink(String id) {
            super(id, System.getProperty("brukerprofil.link.url"));
        }
    }
}
