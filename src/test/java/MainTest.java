import no.nav.dialogarena.config.fasit.FasitUtils;
import no.nav.dialogarena.config.fasit.ServiceUser;
import no.nav.testconfig.ApiAppTest;

import static no.nav.brukerdialog.security.oidc.provider.AzureADB2CConfig.AZUREAD_B2C_DISCOVERY_URL_PROPERTY_NAME;
import static no.nav.brukerdialog.security.oidc.provider.AzureADB2CConfig.AZUREAD_B2C_EXPECTED_AUDIENCE_PROPERTY_NAME;
import static no.nav.dialogarena.config.fasit.FasitUtils.*;
import static no.nav.dialogarena.config.fasit.FasitUtils.Zone.SBS;
import static no.nav.sbl.dialogarena.mininnboks.config.ApplicationConfig.*;
import static no.nav.sbl.dialogarena.mininnboks.config.ServiceConfig.*;
import static no.nav.sbl.dialogarena.mininnboks.provider.LinkService.*;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;
import static no.nav.sbl.util.EnvironmentUtils.setProperty;

public class MainTest {

    private static final String APPLICATION_NAME = "mininnboks-api";
    private static final String MININNBOKS = "mininnboks";

    public static void main(String[] args) {
        ApiAppTest.setupTestContext(ApiAppTest.Config.builder().applicationName(APPLICATION_NAME).build());

        ServiceUser serviceUser = getServiceUser("srvmininnboks", MININNBOKS, SBS);

        setProperty(SECURITYTOKENSERVICE_URL_PROPERTY, getBaseUrl("securityTokenService", SBS), PUBLIC);
        setProperty(SRVMININNBOKS_USERNAME, serviceUser.getUsername(), PUBLIC);
        setProperty(SRVMININNBOKS_PASSWORD, serviceUser.getPassword(), SECRET);

        String serviceGatewayUrl = String.format("https://service-gw-%s.%s",
                FasitUtils.getDefaultEnvironment(),
                FasitUtils.getDefaultDomain(SBS)
        );
        setProperty(INNSYN_HENVENDELSE_WS_URL, serviceGatewayUrl, PUBLIC);
        setProperty(HENVENDELSE_WS_URL, serviceGatewayUrl, PUBLIC);
        setProperty(SEND_INN_HENVENDELSE_WS_URL, serviceGatewayUrl, PUBLIC);
        setProperty(BRUKERPROFIL_V_3_URL, serviceGatewayUrl, PUBLIC);

        setProperty(MININNBOKS_LINK_PROPERTY, resolveLink("mininnboks.link"), PUBLIC);
        setProperty(TEMAVELGER_LINK_PROPERTY, resolveLink("temavelger.link"), PUBLIC);
        setProperty(BRUKERPROFIL_LINK_PROPERTY, resolveLink("brukerprofil.link"), PUBLIC);
        setProperty(SAKSOVERSIKT_LINK_PROPERTY, resolveLink("saksoversikt.link"), PUBLIC);

        setProperty(AZUREAD_B2C_DISCOVERY_URL_PROPERTY_NAME, getBaseUrl("aad_b2c_discovery"), PUBLIC);
        setProperty(AZUREAD_B2C_EXPECTED_AUDIENCE_PROPERTY_NAME, getServiceUser("aad_b2c_clientid", MININNBOKS).getUsername(), PUBLIC);

        Main.main(new String[]{"8455"});
    }

    private static String resolveLink(String baseUrlAlias) {
        return getBaseUrl(baseUrlAlias, getDefaultEnvironment(), getDefaultDomain(SBS), MININNBOKS);
    }

}