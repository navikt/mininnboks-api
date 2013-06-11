package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.CmsContentRetriever;
import no.nav.sbl.dialogarena.minehenvendelser.pages.HomePage;
import no.nav.sbl.dialogarena.webkomponent.footer.FooterPanel;
import no.nav.sbl.dialogarena.webkomponent.innstillinger.InnstillingerPanel;
import no.nav.sbl.dialogarena.webkomponent.navigasjon.NavigasjonPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Locale;
import java.util.Map;

public class BasePage extends WebPage {

    public static final JavaScriptResourceReference JS_RESOURCE = new JavaScriptResourceReference(HomePage.class, "lokal.js");
    public static final CssResourceReference CSS_RESOURCE = new CssResourceReference(HomePage.class, "lokal.css");

    protected static final Locale DEFAULT_LOCALE = new Locale("no");

    @Inject
    protected CmsContentRetriever innholdstekster;

    @Inject
    @Named("navigasjonslink")
    private String navigasjonsLink;

    @Inject
    @Named("footerLinks")
    private Map<String, String> footerLinks;

    public BasePage() {
        add(
                new Label("tittel", innholdstekster.hentTekst("hoved.tittel")),
                new InnstillingerPanel("innstillinger", getInnloggetIsTrueModel()),
                new NavigasjonPanel("navigasjon", navigasjonsLink),
                new FooterPanel("footer", footerLinks, getInnloggetIsTrueModel(), Model.of(false))
        );
    }

    private AbstractReadOnlyModel<Boolean> getInnloggetIsTrueModel() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return true;
            }
        };
    }
}
