import no.nav.apiapp.ApiApp;
import no.nav.common.nais.utils.NaisYamlUtils;
import no.nav.sbl.dialogarena.mininnboks.config.ApplicationConfig;
import no.nav.sbl.dialogarena.test.SystemProperties;
import no.nav.testconfig.ApiAppTest;

import java.util.HashMap;

import static no.nav.sbl.dialogarena.mininnboks.config.ServiceConfig.*;
import static no.nav.sbl.util.EnvironmentUtils.Type.PUBLIC;
import static no.nav.sbl.util.EnvironmentUtils.getRequiredProperty;
import static no.nav.sbl.util.EnvironmentUtils.setProperty;

public class MainTest {

    private static final String APPLICATION_NAME = "mininnboks-api";

    public static void main(String[] args) {
        SystemProperties.setFrom(".vault.properties");
        NaisYamlUtils.loadFromYaml(NaisYamlUtils.getTemplatedConfig(".nais/qa-template.yaml", new HashMap<String, String>() {{
            put("namespace", "q0");
            put("image", "N/A");
            put("version", "N/A");
        }}));

        String serviceGatewayUrl = getRequiredProperty(SERVICEGATEWAY_URL);
        setProperty(INNSYN_HENVENDELSE_WS_URL, serviceGatewayUrl, PUBLIC);
        setProperty(HENVENDELSE_WS_URL, serviceGatewayUrl, PUBLIC);
        setProperty(SEND_INN_HENVENDELSE_WS_URL, serviceGatewayUrl, PUBLIC);

        ApiAppTest.setupTestContext(ApiAppTest.Config.builder().applicationName(APPLICATION_NAME).build());
        ApiApp.runApp(ApplicationConfig.class, new String[]{"8455"});
    }
}
