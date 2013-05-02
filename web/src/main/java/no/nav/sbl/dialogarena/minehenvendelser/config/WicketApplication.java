package no.nav.sbl.dialogarena.minehenvendelser.config;

import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.frontend.MetaTag;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import no.nav.sbl.dialogarena.minehenvendelser.BasePage;
import no.nav.sbl.dialogarena.minehenvendelser.pages.HomePage;
import no.nav.sbl.dialogarena.minehenvendelser.selftest.SelfTestPage;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;

import static no.nav.modig.frontend.FrontendModules.BOOTSTRAP_ACCORDION;
import static no.nav.modig.frontend.FrontendModules.BOOTSTRAP_BUTTON;
import static no.nav.modig.frontend.FrontendModules.BOOTSTRAP_CORE;
import static no.nav.modig.frontend.FrontendModules.BOOTSTRAP_LABELS_AND_BADGES;
import static no.nav.modig.frontend.FrontendModules.BOOTSTRAP_NAVIGATION;
import static no.nav.modig.frontend.FrontendModules.BOOTSTRAP_TOOLTIP;
import static no.nav.modig.frontend.FrontendModules.UNDERSCORE;

/**
 * Kontekst for wicket
 */
public class WicketApplication extends WebApplication {

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
        super.init();
        new FrontendConfigurator()
                .withModules(UNDERSCORE, BOOTSTRAP_CORE, BOOTSTRAP_BUTTON, BOOTSTRAP_LABELS_AND_BADGES, BOOTSTRAP_NAVIGATION, BOOTSTRAP_TOOLTIP, BOOTSTRAP_ACCORDION)
                .addMetas(
                        MetaTag.CHARSET_UTF8,
                        MetaTag.VIEWPORT_SCALE_1,
                        MetaTag.XUA_IE_EDGE)
                .addCss(BasePage.CSS_RESOURCE)
                .addScripts(BasePage.JS_RESOURCE)
                .withResourcePacking(this.usesDeploymentConfig())
                .configure(this);
        new ApplicationSettingsConfig().configure(this);

        mountPage("internal/selftest", SelfTestPage.class);
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
