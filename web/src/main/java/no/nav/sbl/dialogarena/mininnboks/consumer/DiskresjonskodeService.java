package no.nav.sbl.dialogarena.mininnboks.consumer;

/**
 * Tjeneste for å finne diskresjonskode for en person.
 */
public interface DiskresjonskodeService {
    /**
     * Finn diskresjonskoden for en person.
     * @param fnr fødselsnummer til personen det skal hentes for
     * @return 6/7 for diskresjonskode, blank om ingen kode er satt.
     */
    String getDiskresjonskode(String fnr);
}
