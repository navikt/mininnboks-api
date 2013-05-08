package no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk;

import java.util.HashMap;
import java.util.Map;

/**
 * Mockimplementasjon av kodeverk, midlertidig inntil man kan gå mot reell tjeneste
 */
public class KodeverkServiceMock implements KodeverkService {

    public static final String KODEVERK_ID_1 = "kodeForDagpenger";
    public static final String KODEVERK_ID_2 = "kodeForPermitteringsvarsel";
    public static final String KODEVERK_ID_3 = "kodeForArbeidsavtale";
    public static final String KODEVERK_ID_4 = "kodeForEgetVedlegg";
    public static final String KODEVERK_ID_5 = "kodeForForeldrepenger";
    public static final String KODEVERK_ID_6 = "kodeForInntektsopplysninger";
    public static final String KODEVERK_ID_7 = "kodeForKontantstoette";
    public static final String KODEVERK_ID_8 = "kodeForOvergangsstoenad";
    public static final String KODEVERK_ID_9 = "kodeForAvtaleOmDeltBosted";
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

    @Override
    public boolean isEgendefKode(String kodeverkId) {
        if ("kodeForEgetVedlegg".equals(kodeverkId)) {
            return true;
        }
        return false;
    }

    public KodeverkService createMockKodeverk() {
        insertKodeverk(KodeverkServiceMock.KODEVERK_ID_1, "Søknad om dagpenger");
        insertKodeverk(KodeverkServiceMock.KODEVERK_ID_2, "Permitteringsvarsel");
        insertKodeverk(KodeverkServiceMock.KODEVERK_ID_3, "Arbeidsavtale");
        insertKodeverk(KodeverkServiceMock.KODEVERK_ID_4, "Annet: ");
        insertKodeverk(KodeverkServiceMock.KODEVERK_ID_5, "Søknad om foreldrepenger");
        insertKodeverk(KodeverkServiceMock.KODEVERK_ID_6, "Inntektsopplysninger");
        insertKodeverk(KodeverkServiceMock.KODEVERK_ID_7, "Søknad om kontantstøtte");
        insertKodeverk(KodeverkServiceMock.KODEVERK_ID_8, "Stønad om Overgangsstønad");
        insertKodeverk(KodeverkServiceMock.KODEVERK_ID_9, "Avtale om delt bosted");

        return this;
    }
}
