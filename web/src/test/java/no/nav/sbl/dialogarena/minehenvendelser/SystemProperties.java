package no.nav.sbl.dialogarena.minehenvendelser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public final class SystemProperties {

    private SystemProperties() {}

    public static Properties load(String resourcePath) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = properties.getClass().getResourceAsStream(resourcePath);
        properties.load(inputStream);

        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
           System.setProperty((String) entry.getKey(), (String) entry.getValue());
        }
        return properties;
    }

}
