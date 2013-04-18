package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.sbl.dialogarena.common.footer.FooterPanel;
import no.nav.sbl.dialogarena.common.innstillinger.InnstillingerPanel;
import no.nav.sbl.dialogarena.common.navigasjon.NavigasjonPanel;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.CmsContentRetriever;
import no.nav.sbl.dialogarena.minehenvendelser.pages.HomePage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;

public class BasePage extends WebPage {

    public static final JavaScriptResourceReference JS_RESOURCE = new JavaScriptResourceReference(HomePage.class, "lokal.js");
    public static final CssResourceReference CSS_RESOURCE = new CssResourceReference(HomePage.class, "lokal.css");
    @Inject
    protected CmsContentRetriever innholdstekster;

    public BasePage() {
        add(
                new InnstillingerPanel("innstillinger"),
                new NavigasjonPanel("navigasjon"),
                new FooterPanel("footer")
        );
    }
}
