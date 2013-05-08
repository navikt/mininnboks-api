package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.sbl.dialogarena.minehenvendelser.config.WicketApplication;
import no.nav.sbl.dialogarena.minehenvendelser.consumer.util.CmsContentRetriever;
import no.nav.sbl.dialogarena.minehenvendelser.pages.HomePage;
import no.nav.sbl.dialogarena.webkomponent.footer.FooterPanel;
import no.nav.sbl.dialogarena.webkomponent.innstillinger.InnstillingerPanel;
import no.nav.sbl.dialogarena.webkomponent.navigasjon.NavigasjonPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class BasePage extends WebPage {

    public static final JavaScriptResourceReference JQUERY_UI_JS
            = new JavaScriptResourceReference(WicketApplication.class, "js/jquery-ui-1.10.2.custom.min.js");

    public static final JavaScriptResourceReference SHCORE_JS
            = new JavaScriptResourceReference(WicketApplication.class, "js/syntaxhighlighter_3.0.83/scripts/shCore.js");

    public static final JavaScriptResourceReference SHBRUSHXML_JS
            = new JavaScriptResourceReference(WicketApplication.class, "js/syntaxhighlighter_3.0.83/scripts/shBrushXml.js");

    public static final JavaScriptResourceReference SHBRUSHJSCRIPT_JS
            = new JavaScriptResourceReference(WicketApplication.class, "js/syntaxhighlighter_3.0.83/scripts/shBrushJScript.js");

    public static final JavaScriptResourceReference SHBRUSHCSS_JS
            = new JavaScriptResourceReference(WicketApplication.class, "js/syntaxhighlighter_3.0.83/scripts/shBrushCss.js");

    public static final JavaScriptResourceReference LOCAL_JS
            = new JavaScriptResourceReference(WicketApplication.class, "js/eksternflate.js");

    public static final CssResourceReference MODUS_CSS
            = new CssResourceReference(WicketApplication.class, "css/modus.css");

    public static final CssResourceReference JQUERY_UI_CSS
            = new CssResourceReference(WicketApplication.class, "css/jquery-ui-1.10.2.custom.min.css");

    public static final CssResourceReference SHCORE_CSS
            = new CssResourceReference(WicketApplication.class, "js/syntaxhighlighter_3.0.83/styles/shCore.css");

    public static final CssResourceReference SHTHEMEDEFAULT_CSS
            = new CssResourceReference(WicketApplication.class, "js/syntaxhighlighter_3.0.83/styles/shThemeDefault.css");

    public static final PackageResourceReference KORRIGERINGER_LESS
            = new PackageResourceReference(WicketApplication.class, "css/bootstrap-korrigeringer.less");

    public static final PackageResourceReference HJELPEMIDLER_LESS
            = new PackageResourceReference(WicketApplication.class, "css/hjelpemidler.less");

    public static final PackageResourceReference IKONER_LESS
            = new PackageResourceReference(WicketApplication.class, "css/ikoner.less");

    public static final PackageResourceReference KNAPPER_LESS
            = new PackageResourceReference(WicketApplication.class, "css/knapper.less");

    public static final PackageResourceReference MODALVINDU_LESS
            = new PackageResourceReference(WicketApplication.class, "css/modalvindu.less");

    public static final PackageResourceReference NAVIGASJON_LESS
            = new PackageResourceReference(WicketApplication.class, "css/navigasjon.less");

    public static final PackageResourceReference PANELER_LESS
            = new PackageResourceReference(WicketApplication.class, "css/paneler.less");

    public static final PackageResourceReference TYPOGRAFI_LESS
            = new PackageResourceReference(WicketApplication.class, "css/typografi.less");

    public static final PackageResourceReference KODE_LESS
            = new PackageResourceReference(WicketApplication.class, "css/kode-syntax.less");

    public static final PackageResourceReference WICKET_MODAL_LESS
            = new PackageResourceReference(WicketApplication.class, "css/wicket-modal.less");

    public static final PackageResourceReference LOCAL_LESS
            = new PackageResourceReference(WicketApplication.class, "css/eksternflate.less");

    public static final PackageResourceReference DATEPICKER_LESS
            = new PackageResourceReference(WicketApplication.class, "css/datepicker.less");


    public static final JavaScriptResourceReference JS_RESOURCE = new JavaScriptResourceReference(HomePage.class, "lokal.js");
    public static final CssResourceReference CSS_RESOURCE = new CssResourceReference(HomePage.class, "lokal.css");

    @Inject
    protected CmsContentRetriever innholdstekster;

    @Inject
    @Named("navigasjonslink")
    private String navigasjonsLink;

    @Inject
    @Named("dokumentInnsendingBaseUrl")
    protected String dokumentInnsendingBaseUrl;

    @Inject
    @Named("footerLinks")
    private Map<String, String> footerLinks;

    public BasePage() {
        add(
                new InnstillingerPanel("innstillinger", getInnloggetIsTrueModel()),
                new NavigasjonPanel("navigasjon", navigasjonsLink),
                new FooterPanel("footer", footerLinks, getInnloggetIsTrueModel())
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
