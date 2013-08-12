package no.nav.sbl.dialogarena.minehenvendelser;

import javax.inject.Inject;

import no.nav.modig.frontend.ConditionalCssResource;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import no.nav.sbl.dialogarena.minehenvendelser.henvendelser.SporsmalOgSvarSide;
import no.nav.sbl.dialogarena.minehenvendelser.pages.HomePage;
import no.nav.sbl.dialogarena.minehenvendelser.selftest.SelfTestPage;
import no.nav.sbl.dialogarena.webkomponent.felles.SelvbetjeningBasePageMedTilbakemelding;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

/**
 * Kontekst for wicket
 */
public class WicketApplication extends WebApplication {
	
    private static final JavaScriptResourceReference JS_RESOURCE = new JavaScriptResourceReference(HomePage.class, "lokal.js");
    private static final CssResourceReference CSS_RESOURCE = new CssResourceReference(HomePage.class, "lokal.css");
    private static final ConditionalCssResource IE8_CSS_RESOURCE = new ConditionalCssResource(new CssResourceReference(HomePage.class, "ie8-lokal.css"), "screen", "lt IE 9");

    @Inject
    private ApplicationContext applicationContext;

    public static WicketApplication get() {
        return (WicketApplication) Application.get();
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return HomePage.class;
    }

    @Override
    protected void init() {
        SelvbetjeningBasePageMedTilbakemelding.defaultFrontentConfigurator()
                .addScripts(JS_RESOURCE)
                .addCss(CSS_RESOURCE)
                .addLess(new PackageResourceReference(SporsmalOgSvarSide.class, "sporsmal.less"))
                .addConditionalCss(IE8_CSS_RESOURCE)
                .withResourcePacking(this.usesDeploymentConfig())
                .configure(this);
        new ApplicationSettingsConfig().configure(this);

        mountPage("internal/selftest", SelfTestPage.class);
        mountPage("sporsmal", SporsmalOgSvarSide.class);
        Application.get().getRequestLoggerSettings().setRequestLoggerEnabled(true);
        setSpringComponentInjector();

    }

    protected void setSpringComponentInjector() {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
