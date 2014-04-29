package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.frontend.FrontendConfigurator;
import no.nav.modig.wicket.configuration.ApplicationSettingsConfig;
import no.nav.sbl.dialogarena.minehenvendelser.config.utils.LocaleFromWicketSession;
import no.nav.sbl.dialogarena.minehenvendelser.innboks.Innboks;
import no.nav.sbl.dialogarena.minehenvendelser.selftest.SelfTestPage;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.SendSporsmalWizard;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.kvittering.KvitteringPage;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.send.SkrivPage;
import no.nav.sbl.dialogarena.minehenvendelser.sporsmal.tema.VelgTemaPage;
import no.nav.sbl.dialogarena.time.Datoformat;
import no.nav.sbl.dialogarena.webkomponent.innstillinger.LogoutPage;
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
import static no.nav.modig.frontend.MetaTag.CHARSET_UTF8;
import static no.nav.modig.frontend.MetaTag.VIEWPORT_SCALE_1;
import static no.nav.modig.frontend.MetaTag.XUA_IE_EDGE;
import static no.nav.sbl.dialogarena.webkomponent.innstillinger.InnstillingerPanel.INNSTILLINGER_JS;
import static no.nav.sbl.dialogarena.webkomponent.innstillinger.InnstillingerPanel.INNSTILLINGER_LESS;

/**
 * Kontekst for wicket
 */
public class WicketApplication extends WebApplication {

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
                .addLess(INNSTILLINGER_LESS, new PackageResourceReference(Innboks.class, "innboks.less"),
                        new PackageResourceReference(SendSporsmalWizard.class, "sporsmal.less"))
                .addScripts(INNSTILLINGER_JS)
                .withResourcePacking(this.usesDeploymentConfig())
                .configure(this);
        new ApplicationSettingsConfig().configure(this);
        mountPage("innboks", Innboks.class);
        mountPage("sporsmal", SendSporsmalWizard.class);
        mountPage("sporsmal/velgtema", VelgTemaPage.class);
        mountPage("sporsmal/skriv/${tema}", SkrivPage.class);
        mountPage("sporsmal/kvittering", KvitteringPage.class);
        mountPage("internal/selftest", SelfTestPage.class);
        mountPage("loggut", LogoutPage.class);
        Application.get().getRequestLoggerSettings().setRequestLoggerEnabled(true);
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
        getResourceSettings().getStringResourceLoaders().add(0, new CmsResourceLoader(cmsContentRetriever));

        Datoformat.brukLocaleFra(LocaleFromWicketSession.INSTANCE);
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public Session newSession(Request request, Response response) {
        Session session = super.newSession(request, response);
        session.setLocale(new Locale("nb"));
        return session;
    }

}
