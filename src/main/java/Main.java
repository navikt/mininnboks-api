import no.nav.apiapp.ApiApp;
import no.nav.common.nais.utils.NaisUtils;
import no.nav.sbl.dialogarena.mininnboks.config.ApplicationConfig;

import java.nio.file.Paths;

import static no.nav.sbl.dialogarena.mininnboks.config.ApplicationConfig.SRVMININNBOKS_PASSWORD;
import static no.nav.sbl.dialogarena.mininnboks.config.ApplicationConfig.SRVMININNBOKS_USERNAME;
import static no.nav.sbl.dialogarena.mininnboks.config.ServiceConfig.*;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.*;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;

public class Main {
    private static String DEFAULT_SECRETS_BASE_PATH = "/var/run/secrets/nais.io";

    public static void main(String[] args) {
        loadVaultSecrets();
        loadApigwKeys();

        String serviceGatewayUrl = getRequiredProperty(SERVICEGATEWAY_URL);
        setProperty(INNSYN_HENVENDELSE_WS_URL, serviceGatewayUrl, PUBLIC);
        setProperty(HENVENDELSE_WS_URL, serviceGatewayUrl, PUBLIC);
        setProperty(SEND_INN_HENVENDELSE_WS_URL, serviceGatewayUrl, PUBLIC);
        setProperty(BRUKERPROFIL_V_3_URL, serviceGatewayUrl, PUBLIC);

        ApiApp.runApp(ApplicationConfig.class, args);
    }

    private static void loadVaultSecrets() {
        NaisUtils.Credentials serviceUser = NaisUtils.getCredentials("srvmininnboks");
        setProperty(SRVMININNBOKS_USERNAME, serviceUser.username, PUBLIC);
        setProperty(SRVMININNBOKS_PASSWORD, serviceUser.password, SECRET);
    }

    private static void loadApigwKeys() {
        setProperty(PDL_API_APIKEY, getApigwKey("pdl-api"), SECRET);
        setProperty(STS_APIKEY, getApigwKey("security-token-service-token"), SECRET);
    }

    private static String getApigwKey(String producerApp) {
        String location = String.format("%s/apigw/%s/x-nav-apiKey", DEFAULT_SECRETS_BASE_PATH, producerApp);
        return NaisUtils.getFileContent(location);
    }
}
