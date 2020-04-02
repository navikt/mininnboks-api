package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.apiapp.config.StsConfig;
import no.nav.sbl.dialogarena.mininnboks.config.utils.JacksonConfig;
import no.nav.sbl.dialogarena.mininnboks.provider.LinkService;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.HenvendelseController;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.resources.ResourcesController;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.tilgang.TilgangController;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.ubehandletmelding.SporsmalController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

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
        apiAppConfigurator
                .sts(StsConfig.builder()
                        .url(getRequiredProperty(SECURITYTOKENSERVICE_URL_PROPERTY))
                        .username(getRequiredProperty(SRVMININNBOKS_USERNAME))
                        .password(getRequiredProperty(SRVMININNBOKS_PASSWORD))
                        .build()
                )
                .azureADB2CLogin()
                .openAmLogin()
                .objectMapper(JacksonConfig.mapper);
    }

}
