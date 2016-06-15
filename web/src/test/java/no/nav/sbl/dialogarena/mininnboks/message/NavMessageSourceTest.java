package no.nav.sbl.dialogarena.mininnboks.message;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class NavMessageSourceTest {

    private Map<String, String> mockedCmsValues = new HashMap<>();
    {
        mockedCmsValues.put("c:/mininnboks_nb_NO", "mininnboks.key=mininnboks fra disk");
        mockedCmsValues.put("classpath:mininnboks_nb_NO", "mininnboks.key=mininnboks fra minne");
    }

    private NavMessageSource messageSource;
    private boolean diskFilesExist = true;

    @Before
    public void setup() {
        messageSource = new NavMessageSource() {
            @Override
            protected PropertiesHolder getProperties(String filename) {
                Properties mockedProperties = new Properties();

                if (!diskFilesExist && filename.contains("c:/")) {
                    mockedProperties = null;
                } else if (!mockedCmsValues.containsKey(filename)) {
                    mockedProperties = null;
                } else {
                    String mockedValue = mockedCmsValues.get(filename);
                    for (String keyValueString : mockedValue.split(";")) {
                        mockedProperties.put(keyValueString.split("=")[0], keyValueString.split("=")[1]);
                    }
                }

                return new PropertiesHolder(mockedProperties, 0);
            }
        };

        messageSource.setBasenames(
                new NavMessageSource.Bundle("mininnboks", "c:/mininnboks", "classpath:mininnboks")
        );
    }

    @Test
    public void skalHenteSoknadensEgneTeksterOgFellesTeksterNorsk() {
        Properties properties = messageSource.getBundleFor("mininnboks", new Locale("nb", "NO"));
        assertEquals("mininnboks fra disk", properties.getProperty("mininnboks.key"));
    }

    @Test
    public void skalHenteAlleTeksterHvisTypeMangler() {
        Properties properties = messageSource.getBundleFor(null, new Locale("nb", "NO"));
        assertEquals("mininnboks fra disk", properties.getProperty("mininnboks.key"));
    }

    @Test
    public void skalHenteTeksterFraMinneOmDiskIkkeFinnes() {
        diskFilesExist = false;

        Properties properties = messageSource.getBundleFor("mininnboks", new Locale("nb", "NO"));
        assertEquals("mininnboks fra minne", properties.getProperty("mininnboks.key"));

        diskFilesExist = true;

        properties = messageSource.getBundleFor("mininnboks", new Locale("nb", "NO"));
        assertEquals("mininnboks fra disk", properties.getProperty("mininnboks.key"));
    }

}
