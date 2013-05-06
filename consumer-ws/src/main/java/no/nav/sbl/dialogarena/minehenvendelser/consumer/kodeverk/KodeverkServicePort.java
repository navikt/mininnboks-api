package no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk;

public class KodeverkServicePort implements KodeverkService {



    @Override
    public String hentKodeverk(String kodeverkId) {
        return "Kodeverk mangler: " + kodeverkId;
    }

    @Override
    public boolean isEgendefKode(String kodeverkId) {
        return false;
    }
}


