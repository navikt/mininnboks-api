package no.nav.sbl.dialogarena.minehenvendelser;

import no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk.KodeverkService;

import java.util.HashMap;
import java.util.Map;

public class KodeverkServiceMock implements KodeverkService {

    private Map<String, String> koder = new HashMap<>();

    public void insertKodeverk(String key, String value) {
        koder.put(key, value);
    }

    @Override
    public String hentKodeverk(String kodeverkId) {
        if (koder.containsKey(kodeverkId)) {
            return koder.get(kodeverkId);
        }
        return "Kodeverk mangler: " + kodeverkId;
    }
}
