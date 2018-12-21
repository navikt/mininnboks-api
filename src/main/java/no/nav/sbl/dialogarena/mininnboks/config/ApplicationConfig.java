package no.nav.sbl.dialogarena.mininnboks.config;

import no.nav.apiapp.ApiApplication;
import no.nav.apiapp.config.ApiAppConfigurator;
import no.nav.apiapp.config.StsConfig;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.henvendelse.HenvendelseController;
import no.nav.sbl.dialogarena.mininnboks.provider.rest.resources.ResourcesController;
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
        ServiceConfig.class,
        ResourcesController.class,
        SporsmalController.class,
        HenvendelseController.class
})
public class ApplicationConfig implements ApiApplication {

    public static final String SECURITYTOKENSERVICE_URL_PROPERTY = "SECURITYTOKENSERVICE_URL";
    public static final String SRVMININNBOKS_USERNAME = "SRVMININNBOKS_USERNAME";
    public static final String SRVMININNBOKS_PASSWORD = "SRVMININNBOKS_PASSWORD";

    @Override
    public String getContextPath() {
        return "/";
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public void configure(ApiAppConfigurator apiAppConfigurator) {
        apiAppConfigurator
                .sts(StsConfig.builder()
                        .url(getRequiredProperty(SECURITYTOKENSERVICE_URL_PROPERTY))
                        .username(getRequiredProperty(SRVMININNBOKS_USERNAME))
                        .password(getRequiredProperty(SRVMININNBOKS_PASSWORD))
                        .build()
                )
                .azureADB2CLogin();
    }

}