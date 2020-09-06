package no.nav.sbl.dialogarena.mininnboks.config;

public class ApplicationConfig  {

    /*
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
                .withClientId(getRequiredProperty("AAD_B2C_CLIENTID_USERNAME"))
                .withDiscoveryUrl(getRequiredProperty("AAD_B2C_DISCOVERY_URL"))
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
                .objectMapper(JacksonConfig.mapper);
    }
*/
}
