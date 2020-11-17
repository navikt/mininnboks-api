package no.nav.sbl.dialogarena.mininnboks.config;

import lombok.extern.slf4j.Slf4j;
import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.apiapp.config.StsConfig;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.common.oidc.auth.OidcAuthenticatorConfig;
import no.nav.sbl.dialogarena.common.jetty.Jetty;
import no.nav.sbl.dialogarena.mininnboks.config.utils.JacksonConfig;
import no.nav.sbl.dialogarena.mininnboks.provider.LinkService;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.HenvendelseController;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.resources.ResourcesController;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang.TilgangController;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.SporsmalController;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import static no.nav.common.oidc.Constants.AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;

@Configuration
@Import({
        TeksterServiceConfig.class,
        ServiceConfig.class,
        ResourcesController.class,
        SporsmalController.class,
        HenvendelseController.class,
        TilgangController.class
})
@Slf4j
public class ApplicationConfig implements ApiApplication {

    public static final String SECURITYTOKENSERVICE_URL_PROPERTY = "SECURITYTOKENSERVICE_URL";
    public static final String FSS_SRVMININNBOKS_USERNAME = "FSS_SRVMININNBOKS_USERNAME";
    public static final String FSS_SRVMININNBOKS_PASSWORD = "FSS_SRVMININNBOKS_PASSWORD";
    public static final String SRVMININNBOKS_USERNAME = "SRVMININNBOKS_USERNAME";
    public static final String SRVMININNBOKS_PASSWORD = "SRVMININNBOKS_PASSWORD";

    @Override
    public String getContextPath() {
        return "/";
    }

    @Override
    public String getApiBasePath() {
        return "/";
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        LinkService.touch();

        OidcAuthenticatorConfig azureADB2CConfig = new OidcAuthenticatorConfig()
                // LOGINSERVICE-variables comes from `loginservice-idporten` configmap specified in nais-yml files
                .withClientId(getRequiredProperty("LOGINSERVICE_IDPORTEN_AUDIENCE"))
                .withDiscoveryUrl(getRequiredProperty("LOGINSERVICE_IDPORTEN_DISCOVERY_URL"))
                .withIdentType(IdentType.EksternBruker)
                .withIdTokenCookieName(AZURE_AD_B2C_ID_TOKEN_COOKIE_NAME);

        apiAppConfigurator
                .sts(StsConfig.builder()
                        .url(getRequiredProperty(SECURITYTOKENSERVICE_URL_PROPERTY))
                        .username(getRequiredProperty(SRVMININNBOKS_USERNAME))
                        .password(getRequiredProperty(SRVMININNBOKS_PASSWORD))
                        .build()
                )
                .addOidcAuthenticator(azureADB2CConfig)
                .customizeJetty((Jetty jetty) -> {
                    ThreadPool threadPool = jetty.server.getThreadPool();
                    if (threadPool instanceof ThreadPool.SizedThreadPool) {
                        ((ThreadPool.SizedThreadPool) threadPool).setMinThreads(100);
                        ((ThreadPool.SizedThreadPool) threadPool).setMaxThreads(600);
                        log.info("Customizing Jetty Threadpool, min: 100 max: 600");
                    } else {
                        log.warn(String.format(
                                "Jetty Threadpool[%s] is not sized",
                                threadPool.getClass().getSimpleName()
                        ));
                    }
                })
                .objectMapper(JacksonConfig.mapper);
    }

}
