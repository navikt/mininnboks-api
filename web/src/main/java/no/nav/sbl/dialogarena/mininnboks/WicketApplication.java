package no.nav.sbl.dialogarena.mininnboks;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import no.nav.modig.wicket.selftest.HealthCheck;
import no.nav.modig.wicket.selftest.JsonResourceReference;
import no.nav.sbl.dialogarena.mininnboks.config.utils.LocaleFromWicketSession;
import no.nav.sbl.dialogarena.mininnboks.innboks.Innboks;
import no.nav.sbl.dialogarena.mininnboks.selftest.SelfTestPage;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.kvittering.KvitteringPage;
import no.nav.sbl.dialogarena.mininnboks.sporsmal.send.SkrivPage;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;

import javax.inject.Inject;
import java.util.Locale;

import static no.nav.modig.frontend.FrontendModules.EKSTERNFLATE;
import static no.nav.modig.frontend.MetaTag.*;

/**
 * Kontekst for wicket
 */
public class WicketApplication extends WebApplication {

    public static final String INNBOKS_PATH = "innboks";

    @Inject
    private ApplicationContext applicationContext;
    @Inject
    private CmsContentRetriever cmsContentRetriever;

    public static WicketApplication get() {
        return (WicketApplication) Application.get();
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return Innboks.class;
    }
    
    @Override
    protected void init() {
        super.init();
        new FrontendConfigurator()
                .withModules(EKSTERNFLATE)
                .addMetas(CHARSET_UTF8, VIEWPORT_SCALE_1, XUA_IE_EDGE)
                .addLess(new PackageResourceReference(Innboks.class, "innboks.less"),
                        new PackageResourceReference(SkrivPage.class, "sporsmal.less"))
                .withResourcePacking(this.usesDeploymentConfig())
                .addScripts(SkrivPage.JQUERY_JS)
                .addCss(SkrivPage.JQUERY_CSS)
                .configure(this);
        new ApplicationSettingsConfig().configure(this);
        mountPage(INNBOKS_PATH, Innboks.class);
        mountPage("sporsmal/skriv/${temagruppe}", SkrivPage.class);
        mountPage("sporsmal/kvittering", KvitteringPage.class);
        mountPage("internal/isAlive", HealthCheck.class);
        mountPage("internal/selftest", SelfTestPage.class);
        mountResource("internal/selftest.json", new JsonResourceReference(SelfTestPage.class));
        get().getRequestLoggerSettings().setRequestLoggerEnabled(true);
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
        getResourceSettings().getStringResourceLoaders().add(0, new CmsResourceLoader(cmsContentRetriever));
        Datoformat.brukLocaleFra(LocaleFromWicketSession.INSTANCE);
    }

    @Override
    public Session newSession(Request request, Response response) {
        Session session = super.newSession(request, response);
        session.setLocale(new Locale("nb"));
        return session;
    }

}
