package no.nav.sbl.dialogarena.minehenvendelser.consumer.kodeverk;


/**
 * Utilklasse for interaksjon med kodeverk
 */
public interface KodeverkService {

    String hentKodeverk(String kodeverkId);
    boolean isEgendefKode(String kodeverkId);

}
