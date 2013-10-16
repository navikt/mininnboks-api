package no.nav.sbl.dialogarena.minehenvendelser.person.adresse;

/**
 * Kodeverksinterface for oppslag på adresse-elementer.
 */
public interface Adressekodeverk {

    String LANDKODE_NORGE = "NOR";

    String getLand(String landkode);

    String getPoststed(String postnummer);

}
