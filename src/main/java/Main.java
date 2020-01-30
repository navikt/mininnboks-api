import no.nav.apiapp.ApiApp;
import no.nav.common.nais.utils.NaisUtils;
import no.nav.sbl.dialogarena.mininnboks.config.ApplicationConfig;

import static no.nav.sbl.dialogarena.mininnboks.config.ApplicationConfig.SRVMININNBOKS_PASSWORD;
import static no.nav.sbl.dialogarena.mininnboks.config.ApplicationConfig.SRVMININNBOKS_USERNAME;
import static no.nav.sbl.dialogarena.mininnboks.config.ServiceConfig.*;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.*;
import static no.nav.sbl.util.EnvironmentUtils.Type.SECRET;

public class Main {
    public static void main(String[] args) {
        loadVaultSecrets();

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

}
