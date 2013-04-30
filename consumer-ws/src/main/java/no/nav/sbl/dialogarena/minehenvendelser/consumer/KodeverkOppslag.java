package no.nav.sbl.dialogarena.minehenvendelser.consumer;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilklasse for interaksjon med kodeverk
 */
public class KodeverkOppslag {

    private Map<String, String> koder = new HashMap<>();

    public void insertKodeverk(String key, String value) {
        koder.put(key, value);
    }

    public String hentKodeverk(String kodeverkId) {
        if (koder.containsKey(kodeverkId)) {
            return koder.get(kodeverkId);
        }
        return "Kodeverk mangler: " + kodeverkId;
    }

}


