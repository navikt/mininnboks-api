import no.nav.apiapp.ApiApp;
import no.nav.sbl.dialogarena.mininnboks.config.ApplicationConfig;

import static no.nav.sbl.dialogarena.mininnboks.config.ServiceConfig.*;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.*;

public class Main {

    private static final String SERVICEGATEWAY_URL = "SERVICEGATEWAY_URL";

    public static void main(String[] args) {

        setupWsProperty(INNSYN_HENVENDELSE_WS_URL);
        setupWsProperty(HENVENDELSE_WS_URL);
        setupWsProperty(SEND_INN_HENVENDELSE_WS_URL);
        setupWsProperty(BRUKERPROFIL_V_3_URL);

        ApiApp.runApp(ApplicationConfig.class, args);
    }

    private static void setupWsProperty(String propertyName) {
        if(!getOptionalProperty(propertyName).isPresent()){
            setProperty(propertyName, getRequiredProperty(SERVICEGATEWAY_URL), PUBLIC);
        }
    }

}
